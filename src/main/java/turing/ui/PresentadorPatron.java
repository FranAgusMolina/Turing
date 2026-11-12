package turing.ui;

import java.util.Random;

import javafx.concurrent.Task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import turing.controlador.CoordinadorApp;
import turing.modelo.MaquinaTuring;
import turing.negocio.automata.CodificadorCinta;
import turing.negocio.automata.EntradaAutomata;
import turing.negocio.automata.EvolucionAutomata;
import turing.negocio.motor.MotorTuring;
import turing.negocio.regla.ReglaLocal;
import turing.ui.componentes.PanelPatron;

/**
 * Presentador de la pestaña "Patrón triangular". Ejecuta la máquina de Turing en un
 * hilo de fondo, dibuja las generaciones a medida que aparecen en la cinta y al final
 * las verifica contra la simulación directa.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class PresentadorPatron {

    private static final Logger LOG = LogManager.getLogger(PresentadorPatron.class);
    private static final long PASOS_POR_TANDA = 250_000;

    private final PanelPatron panel;
    private final CoordinadorApp coordinador;
    private ReglaLocal regla;
    private Task<EvolucionAutomata> tarea;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param panel       vista
     * @param coordinador coordinador de la aplicación
     * @param regla       regla inicial
     */
    public PresentadorPatron(PanelPatron panel, CoordinadorApp coordinador, ReglaLocal regla) {
        this.panel = panel;
        this.coordinador = coordinador;
        this.regla = regla;
        panel.getBotonGenerar().setOnAction(evento -> generar());
        panel.getBotonCancelar().setOnAction(evento -> cancelar());
    }

    /**
     * @param nueva regla con la que se generarán los próximos patrones
     */
    public void setRegla(ReglaLocal nueva) {
        cancelar();
        this.regla = nueva;
        panel.getVerificacion().setText("");
        panel.getEstado().setText(regla.getNombre() + " seleccionada. Generá el patrón para verlo.");
    }

    private void generar() {
        cancelar();
        EntradaAutomata entrada = crearEntrada();
        MaquinaTuring maquina = coordinador.obtenerMaquina(regla);
        MotorTuring motor = coordinador.prepararEjecucion(maquina, CodificadorCinta.codificar(entrada), 0);

        tarea = crearTarea(motor, entrada);
        tarea.valueProperty().addListener((obs, vieja, nueva) -> {
            if (nueva != null) {
                panel.getLienzo().mostrar(nueva, entrada.longitud(), entrada.generaciones() + 1);
            }
        });
        panel.getProgreso().progressProperty().bind(tarea.progressProperty());
        panel.getEstado().textProperty().bind(tarea.messageProperty());
        tarea.setOnSucceeded(evento -> terminar(entrada, tarea.getValue(), motor));
        tarea.setOnFailed(evento -> {
            LOG.error("Error generando el patrón", tarea.getException());
            liberarControles();
            panel.getEstado().setText("Error: " + tarea.getException().getMessage());
        });
        tarea.setOnCancelled(evento -> {
            liberarControles();
            panel.getEstado().setText("Generación cancelada.");
        });

        panel.getBotonGenerar().setDisable(true);
        panel.getBotonCancelar().setDisable(false);
        panel.getVerificacion().setText("");
        Thread hilo = new Thread(tarea, "generador-patron");
        hilo.setDaemon(true);
        hilo.start();
    }

    private Task<EvolucionAutomata> crearTarea(MotorTuring motor, EntradaAutomata entrada) {
        return new Task<>() {
            @Override
            protected EvolucionAutomata call() {
                long inicio = System.nanoTime();
                EvolucionAutomata result = null;
                while (!motor.getResultado().estaDetenida() && !isCancelled()) {
                    motor.ejecutar(PASOS_POR_TANDA);
                    result = CodificadorCinta.leerEvolucion(motor.getCinta().contenido());
                    updateValue(result);
                    updateProgress(Math.max(0, result.cantidad() - 1), entrada.generaciones());
                    updateMessage(String.format("Generación %d de %d · %,d pasos · %.1f s",
                            Math.max(0, result.cantidad() - 1), entrada.generaciones(), motor.getPasos(),
                            (System.nanoTime() - inicio) / 1e9));
                }
                return result;
            }
        };
    }

    private void terminar(EntradaAutomata entrada, EvolucionAutomata obtenida, MotorTuring motor) {
        liberarControles();
        panel.getLienzo().mostrar(obtenida, entrada.longitud(), entrada.generaciones() + 1);
        boolean coincide = coordinador.simularDirecto(entrada, regla).mismasGeneraciones(obtenida);
        panel.getEstado().setText(String.format("%s · N=%d · K=%d · %,d pasos · %s",
                regla.getNombre(), entrada.longitud(), entrada.generaciones(), motor.getPasos(),
                motor.getResultado().getDescripcion()));
        panel.getVerificacion().setText(coincide
                ? "✔ Coincide con la simulación directa"
                : "✘ No coincide con la simulación directa");
        panel.getVerificacion().getStyleClass().removeAll("ok", "falla");
        panel.getVerificacion().getStyleClass().add(coincide ? "ok" : "falla");
    }

    private void cancelar() {
        if (tarea != null && tarea.isRunning()) {
            tarea.cancel();
        }
    }

    private void liberarControles() {
        panel.getProgreso().progressProperty().unbind();
        panel.getEstado().textProperty().unbind();
        panel.getBotonGenerar().setDisable(false);
        panel.getBotonCancelar().setDisable(true);
    }

    private EntradaAutomata crearEntrada() {
        int n = panel.getLongitud().getValue();
        int k = panel.getGeneraciones().getValue();
        int[] celdas = new int[n];
        String inicial = panel.getInicial().getValue();
        if (PanelPatron.INICIAL_ALEATORIA.equals(inicial)) {
            Random random = new Random();
            for (int anIndex = 0; anIndex < n; anIndex++) {
                celdas[anIndex] = random.nextInt(2);
            }
        } else if (PanelPatron.INICIAL_CENTRO.equals(inicial)) {
            celdas[n / 2] = 1;
        } else {
            celdas[n - 1] = 1;
        }
        return new EntradaAutomata(k, celdas);
    }
}
