package turing.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import turing.controlador.CoordinadorApp;
import turing.negocio.regla.ReglaLocal;
import turing.ui.componentes.PanelSimulacion;

/**
 * Implementación JavaFX de la interfaz. Arma la ventana: encabezado con la Regla 110
 * y la simulación como única pantalla.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class InterfazJavaFXImpl implements Interfaz {

    private static final Logger LOG = LogManager.getLogger(InterfazJavaFXImpl.class);

    private CoordinadorApp coordinador;

    // Los controles se crean en construir(): JavaFX todavía no está iniciado cuando
    // el coordinador instancia esta clase.
    private Label subtitulo;

    @Override
    public void setCoordinador(CoordinadorApp coordinador) {
        this.coordinador = coordinador;
    }

    @Override
    public void iniciar() {
        JavaFXLauncher.launchJavaFX(this);
    }

    /**
     * Construye la ventana principal. La invoca JavaFXLauncher en el hilo de JavaFX.
     *
     * @param stage ventana principal
     */
    public void construir(Stage stage) {
        ReglaLocal regla = coordinador.crearRegla();
        subtitulo = new Label();

        PanelSimulacion panelSimulacion = new PanelSimulacion();
        new PresentadorSimulacion(panelSimulacion, coordinador, regla);
        mostrarRegla(regla);

        BorderPane raiz = new BorderPane(panelSimulacion);
        raiz.setTop(construirEncabezado());
        raiz.getStyleClass().add("raiz");

        Scene escena = new Scene(raiz, 1360, 860);
        escena.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
        stage.setTitle("Máquina de Turing · Regla 110");
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        stage.setScene(escena);
        stage.show();
        LOG.info("Ventana principal visible");
    }

    private HBox construirEncabezado() {
        Label titulo = new Label("Máquina de Turing");
        titulo.getStyleClass().add("titulo");
        subtitulo.getStyleClass().add("subtitulo-app");
        VBox textos = new VBox(2, titulo, subtitulo);

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox result = new HBox(14, textos, espacio);
        result.setAlignment(Pos.CENTER_LEFT);
        result.setPadding(new Insets(14, 20, 14, 20));
        result.getStyleClass().add("encabezado");
        return result;
    }

    private void mostrarRegla(ReglaLocal regla) {
        subtitulo.setText("Autómata celular elemental · " + regla.getNombre() + " · fronteras cíclicas · "
                + tablaDeVerdad(regla));
    }

    private String tablaDeVerdad(ReglaLocal regla) {
        StringBuilder result = new StringBuilder();
        for (int aPattern = 7; aPattern >= 0; aPattern--) {
            result.append(aPattern >> 2 & 1).append(aPattern >> 1 & 1).append(aPattern & 1)
                    .append('→').append(regla.siguiente(aPattern >> 2 & 1, aPattern >> 1 & 1, aPattern & 1))
                    .append(aPattern > 0 ? "  " : "");
        }
        return result.toString();
    }
}
