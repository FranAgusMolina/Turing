package turing.ui;

import java.util.Map;

import turing.aplicacion.Constantes;
import turing.controlador.CoordinadorApp;
import turing.modelo.MaquinaTuring;
import turing.negocio.bloques.BloqueTuring;
import turing.negocio.bloques.FabricaBloques;
import turing.negocio.motor.MotorTuring;
import turing.ui.componentes.PanelBloques;

/**
 * Presentador del laboratorio de bloques: arma la notación del bloque elegido,
 * lo compila como máquina independiente y lo ejecuta sobre la cinta indicada.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class PresentadorBloques {

    /** Blanco que se puede tipear en el campo de texto en lugar de Δ. */
    private static final char BLANCO_TECLADO = '_';

    /** Cinta de ejemplo y posición de la cabeza para cada bloque del catálogo. */
    private static final Map<String, Object[]> EJEMPLOS = Map.of(
            "L", new Object[]{"0110#", 2},
            "R", new Object[]{"0110#", 2},
            "x", new Object[]{"0000", 1},
            "Rx", new Object[]{"0110#01", 0},
            "Lx", new Object[]{"#0110", 4},
            "SR", new Object[]{"0110_", 3},
            "SL", new Object[]{"_0110", 1});

    private final PanelBloques panel;
    private final CoordinadorApp coordinador;
    private final EjecutorAnimado ejecutor;
    private MotorTuring motor;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param panel       vista
     * @param coordinador coordinador de la aplicación
     */
    public PresentadorBloques(PanelBloques panel, CoordinadorApp coordinador) {
        this.panel = panel;
        this.coordinador = coordinador;
        this.ejecutor = new EjecutorAnimado(this::refrescar, () -> { });
        this.ejecutor.setPasosPorSegundo(6);
        conectarEventos();
        panel.getCatalogo().getSelectionModel().selectFirst();
    }

    private void conectarEventos() {
        panel.getCatalogo().getItems().setAll(FabricaBloques.CATALOGO);
        panel.getCatalogo().getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                Object[] ejemplo = EJEMPLOS.get(nuevo);
                panel.getCampoCinta().setText((String) ejemplo[0]);
                panel.getPosicion().getValueFactory().setValue((Integer) ejemplo[1]);
                panel.getParametro().setDisable(!nuevo.contains("x"));
                if ("x".equals(nuevo)) {
                    panel.getParametro().setText("1");
                } else if (nuevo.contains("x")) {
                    panel.getParametro().setText("#");
                }
                cargar();
            }
        });
        panel.getParametro().setOnAction(evento -> cargar());
        panel.getBotonCargar().setOnAction(evento -> cargar());
        panel.getBotonPaso().setOnAction(evento -> {
            if (motor != null) {
                ejecutor.detener();
                motor.paso();
                refrescar();
            }
        });
        panel.getBotonEjecutar().setOnAction(evento -> ejecutor.iniciar(null));
    }

    private String notacion() {
        String elegido = panel.getCatalogo().getSelectionModel().getSelectedItem();
        String parametro = panel.getParametro().getText().trim().replace(BLANCO_TECLADO, Constantes.BLANCO);
        return switch (elegido) {
            case "x" -> parametro;
            case "Rx" -> "R" + parametro;
            case "Lx" -> "L" + parametro;
            default -> elegido;
        };
    }

    private void cargar() {
        ejecutor.detener();
        String cinta = panel.getCampoCinta().getText().replace(BLANCO_TECLADO, Constantes.BLANCO);
        try {
            BloqueTuring bloque = FabricaBloques.crear(notacion());
            MaquinaTuring maquina = coordinador.compilarBloque(bloque.getNotacion());
            validarCinta(cinta, maquina);
            motor = coordinador.prepararEjecucion(maquina, cinta, panel.getPosicion().getValue());
            ejecutor.setMotor(motor);
            panel.mostrarError(null);
            panel.getTitulo().setText("Bloque " + bloque.getNotacion());
            panel.getDescripcion().setText(bloque.getDescripcion());
            panel.getTabla().mostrar(maquina);
            refrescar();
        } catch (IllegalArgumentException e) {
            panel.mostrarError(e.getMessage());
        }
    }

    private void validarCinta(String cinta, MaquinaTuring maquina) {
        for (char aSymbol : cinta.toCharArray()) {
            if (!maquina.getAlfabeto().contiene(aSymbol)) {
                throw new IllegalArgumentException("El símbolo '" + aSymbol + "' no pertenece a Σ = "
                        + maquina.getAlfabeto() + " (usá _ para el blanco)");
            }
        }
    }

    private void refrescar() {
        if (motor == null) {
            return;
        }
        panel.getVistaCinta().mostrar(motor.getCinta(), motor.getEstadoActual().nombre());
        panel.getEstado().setText(String.format("Estado %s   ·   pasos %d   ·   %s   ·   última: %s",
                motor.getEstadoActual().nombre(), motor.getPasos(), motor.getResultado().getDescripcion(),
                motor.getUltimaTransicion() == null ? "–" : motor.getUltimaTransicion()));
        panel.getTabla().resaltar(motor.getUltimaTransicion());
    }
}
