package turing.ui;

import java.util.List;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.concurrent.Task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import turing.aplicacion.Constantes;
import turing.controlador.CoordinadorApp;
import turing.negocio.regla.ReglaLocal;
import turing.servicio.ResultadoCaso;
import turing.ui.componentes.PanelCasos;
import turing.ui.componentes.PanelCasos.FilaCaso;

/**
 * Presentador de la pestaña "Casos de prueba": carga el archivo, ejecuta todos los
 * casos con la máquina de la Regla 110 en segundo plano y muestra el veredicto.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class PresentadorCasos {

    private static final Logger LOG = LogManager.getLogger(PresentadorCasos.class);

    private final PanelCasos panel;
    private final CoordinadorApp coordinador;
    private final ReglaLocal regla;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param panel             vista
     * @param coordinador       coordinador de la aplicación
     * @param abrirEnSimulacion acción que lleva una cinta a la pestaña de simulación
     */
    public PresentadorCasos(PanelCasos panel, CoordinadorApp coordinador, Consumer<String> abrirEnSimulacion) {
        this.panel = panel;
        this.coordinador = coordinador;
        this.regla = coordinador.crearRegla(Constantes.REGLA_POR_DEFECTO);
        panel.getBotonRecargar().setOnAction(evento -> cargar());
        panel.getBotonEjecutar().setOnAction(evento -> ejecutarTodos());
        panel.getBotonAbrir().setOnAction(evento -> abrirSeleccionado(abrirEnSimulacion));
        panel.getTabla().setOnMouseClicked(evento -> {
            if (evento.getClickCount() == 2) {
                abrirSeleccionado(abrirEnSimulacion);
            }
        });
        cargar();
    }

    private void cargar() {
        try {
            List<FilaCaso> filas = coordinador.getCasosPrueba().stream().map(FilaCaso::new).toList();
            panel.getTabla().getItems().setAll(filas);
            panel.getResumen().setText(filas.size() + " casos cargados");
        } catch (RuntimeException e) {
            LOG.error("No se pudieron cargar los casos", e);
            panel.getResumen().setText("Error: " + e.getMessage());
        }
    }

    private void ejecutarTodos() {
        List<FilaCaso> filas = List.copyOf(panel.getTabla().getItems());
        panel.getBotonEjecutar().setDisable(true);
        Task<Integer> tarea = new Task<>() {
            @Override
            protected Integer call() {
                int correctos = 0;
                for (FilaCaso aRow : filas) {
                    ResultadoCaso aResult = coordinador.ejecutarCaso(aRow.getCaso(), regla);
                    correctos += aResult.correcto() ? 1 : 0;
                    Platform.runLater(() -> mostrar(aRow, aResult));
                }
                return correctos;
            }
        };
        tarea.setOnSucceeded(evento -> {
            panel.getBotonEjecutar().setDisable(false);
            int correctos = tarea.getValue();
            panel.getResumen().setText(correctos + " / " + filas.size() + " casos correctos");
            panel.getResumen().getStyleClass().removeAll("ok", "falla");
            panel.getResumen().getStyleClass().add(correctos == filas.size() ? "ok" : "falla");
        });
        tarea.setOnFailed(evento -> {
            panel.getBotonEjecutar().setDisable(false);
            LOG.error("Error ejecutando los casos", tarea.getException());
        });
        Thread hilo = new Thread(tarea, "ejecutor-casos");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void mostrar(FilaCaso fila, ResultadoCaso resultado) {
        fila.obtenidaProperty().set(resultado.error().isEmpty() ? resultado.obtenida() : resultado.error());
        fila.pasosProperty().set(resultado.pasos() == 0 ? "–" : String.format("%,d", resultado.pasos()));
        String veredicto;
        if (resultado.correcto()) {
            veredicto = resultado.error().isEmpty() ? "✔ correcto" : "✔ rechazada";
        } else {
            veredicto = "✘ incorrecto";
        }
        fila.veredictoProperty().set(veredicto);
        panel.getTabla().refresh();
    }

    private void abrirSeleccionado(Consumer<String> abrirEnSimulacion) {
        FilaCaso seleccionada = panel.getTabla().getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            abrirEnSimulacion.accept(seleccionada.getCaso().entrada());
        }
    }
}
