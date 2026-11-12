package turing.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import turing.aplicacion.Constantes;
import turing.controlador.CoordinadorApp;
import turing.modelo.Cinta;
import turing.modelo.Transicion;
import turing.negocio.automata.CodificadorCinta;
import turing.negocio.automata.EntradaAutomata;
import turing.negocio.automata.EvolucionAutomata;
import turing.negocio.automata.ValidadorEntrada;
import turing.negocio.motor.MotorTuring;
import turing.negocio.regla.ReglaLocal;
import turing.ui.componentes.PanelSimulacion;

/**
 * Presentador de la pestaña "Simulación": carga la cinta, maneja los controles de
 * ejecución y traduce la configuración instantánea del motor a la vista.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class PresentadorSimulacion {

    private static final Logger LOG = LogManager.getLogger(PresentadorSimulacion.class);

    /** Entrar al nodo VOLVER_K significa que se acaba de cerrar una generación con #. */
    private static final String BLOQUE_TRAS_CIERRE = "VOLVER_K ·";

    private static final Map<String, String> EJEMPLOS = new LinkedHashMap<>();

    static {
        EJEMPLOS.put("Enunciado 1 · K=4, N=15", "100#000000010000000#");
        EJEMPLOS.put("Enunciado 2 · K=14, N=8", "1110#01101001#");
        EJEMPLOS.put("Triángulo · K=20, N=24", "10100#000000000000000000000001#");
        EJEMPLOS.put("Frontera cíclica · K=1", "1#11000001#");
        EJEMPLOS.put("K = 0 (no calcula)", "0#10110010#");
    }

    private final PanelSimulacion panel;
    private final CoordinadorApp coordinador;
    private final EjecutorAnimado ejecutor;

    private ReglaLocal regla;
    private String entradaCargada;
    private EntradaAutomata entrada;
    private MotorTuring motor;
    private int generacionesMostradas = -1;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param panel       vista
     * @param coordinador coordinador de la aplicación
     * @param regla       regla inicial
     */
    public PresentadorSimulacion(PanelSimulacion panel, CoordinadorApp coordinador, ReglaLocal regla) {
        this.panel = panel;
        this.coordinador = coordinador;
        this.regla = regla;
        this.ejecutor = new EjecutorAnimado(this::refrescar, this::alPausar);
        conectarEventos();
        actualizarVelocidad();
        cargarTexto(EJEMPLOS.values().iterator().next());
    }

    private void conectarEventos() {
        panel.getEjemplos().getItems().setAll(EJEMPLOS.keySet());
        panel.getEjemplos().setOnAction(evento -> {
            String elegido = panel.getEjemplos().getValue();
            if (elegido != null) {
                cargarTexto(EJEMPLOS.get(elegido));
            }
        });
        panel.getBotonCargar().setOnAction(evento -> cargar(panel.getCampoCinta().getText().trim()));
        panel.getBotonReiniciar().setOnAction(evento -> {
            if (entradaCargada != null) {
                cargar(entradaCargada);
            }
        });
        panel.getBotonPaso().setOnAction(evento -> paso());
        panel.getBotonReproducir().setOnAction(evento -> alternarReproduccion());
        panel.getBotonGeneracion().setOnAction(evento -> correrHasta(true));
        panel.getBotonFinal().setOnAction(evento -> correrHasta(false));
        panel.getVelocidad().valueProperty().addListener((obs, viejo, nuevo) -> actualizarVelocidad());
    }

    /**
     * Carga una cinta desde otra pestaña (por ejemplo, un caso de prueba).
     *
     * @param texto cinta de entrada
     */
    public void cargarTexto(String texto) {
        panel.getCampoCinta().setText(texto);
        cargar(texto);
    }

    /**
     * Cambia la regla y recarga la cinta actual con la nueva máquina.
     *
     * @param nueva nueva regla
     */
    public void setRegla(ReglaLocal nueva) {
        this.regla = nueva;
        panel.getTablaTransiciones().mostrar(coordinador.obtenerMaquina(regla));
        if (entradaCargada != null) {
            cargar(entradaCargada);
        }
    }

    private void cargar(String texto) {
        ejecutor.detener();
        Optional<String> error = ValidadorEntrada.validar(texto);
        if (error.isPresent()) {
            panel.mostrarMensaje(error.get(), true);
            return;
        }
        panel.mostrarMensaje(null, false);
        entradaCargada = texto;
        entrada = CodificadorCinta.decodificar(texto);
        motor = coordinador.prepararEjecucion(coordinador.obtenerMaquina(regla), texto, 0);
        ejecutor.setMotor(motor);
        panel.getTablaTransiciones().mostrar(motor.getMaquina());
        generacionesMostradas = -1;
        LOG.info("Cinta cargada: {} con {}", texto, regla.getNombre());
        refrescar();
        panel.getTablaTransiciones().resaltar(null);
    }

    private void paso() {
        if (motor == null) {
            return;
        }
        ejecutor.detener();
        motor.paso();
        refrescar();
        panel.getTablaTransiciones().resaltar(motor.getUltimaTransicion());
    }

    private void alternarReproduccion() {
        if (ejecutor.isActivo()) {
            ejecutor.detener();
            return;
        }
        actualizarVelocidad();
        ejecutor.iniciar(null);
        sincronizarBotonReproducir();
    }

    private void correrHasta(boolean soloUnaGeneracion) {
        if (motor == null) {
            return;
        }
        ejecutor.detener();
        ejecutor.setPasosPorSegundo(Double.POSITIVE_INFINITY);
        ejecutor.iniciar(soloUnaGeneracion
                ? transicion -> !transicion.origen().bloque().startsWith(BLOQUE_TRAS_CIERRE)
                        && transicion.destino().bloque().startsWith(BLOQUE_TRAS_CIERRE)
                : null);
        sincronizarBotonReproducir();
    }

    private void alPausar() {
        actualizarVelocidad();
        sincronizarBotonReproducir();
        if (motor != null) {
            panel.getTablaTransiciones().resaltar(motor.getUltimaTransicion());
        }
    }

    private void sincronizarBotonReproducir() {
        boolean activo = ejecutor.isActivo();
        panel.getBotonReproducir().setSelected(activo);
        panel.getBotonReproducir().setText(activo ? "⏸ Pausa" : "▶ Reproducir");
    }

    private void actualizarVelocidad() {
        double exponente = panel.getVelocidad().getValue();
        boolean maxima = exponente >= panel.getVelocidad().getMax() - 0.01;
        double pasosPorSegundo = maxima ? Double.POSITIVE_INFINITY : Math.pow(10, exponente);
        ejecutor.setPasosPorSegundo(pasosPorSegundo);
        panel.getValorVelocidad().setText(maxima ? "máxima" : String.format("%,.0f pasos/s", pasosPorSegundo));
    }

    /**
     * Vuelca la configuración instantánea del motor en la vista.
     */
    private void refrescar() {
        if (motor == null) {
            return;
        }
        Cinta cinta = motor.getCinta();
        panel.getVistaCinta().mostrar(cinta, motor.getEstadoActual().nombre());
        panel.getValorEstado().setText(motor.getEstadoActual().nombre());
        panel.getValorBloque().setText(motor.getEstadoActual().bloque());
        panel.getValorPasos().setText(String.format("%,d", motor.getPasos()));
        Transicion ultima = motor.getUltimaTransicion();
        panel.getValorTransicion().setText(ultima == null ? "–" : ultima.toString());
        panel.getValorResultado().setText(motor.getResultado().getDescripcion());

        String contenido = cinta.contenido();
        panel.getValorContador().setText(contadorK(contenido));
        EvolucionAutomata evolucion = CodificadorCinta.leerEvolucion(contenido);
        panel.getValorGeneracion().setText(Math.max(0, evolucion.cantidad() - 1) + " de " + entrada.generaciones());
        panel.getLienzo().mostrar(evolucion, entrada.longitud(), entrada.generaciones() + 1);
        if (evolucion.cantidad() != generacionesMostradas) {
            generacionesMostradas = evolucion.cantidad();
            panel.getTextoGeneraciones().setText(evolucion.toString());
            panel.getTextoGeneraciones().positionCaret(panel.getTextoGeneraciones().getLength());
        }
    }

    private String contadorK(String contenido) {
        int separador = contenido.indexOf(Constantes.SEPARADOR);
        if (separador <= 0) {
            return "–";
        }
        String binario = contenido.substring(0, separador);
        return binario + "₂ = " + Long.parseLong(binario, 2);
    }
}
