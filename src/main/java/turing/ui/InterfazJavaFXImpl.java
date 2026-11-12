package turing.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import turing.aplicacion.Constantes;
import turing.controlador.CoordinadorApp;
import turing.negocio.regla.ReglaLocal;
import turing.ui.componentes.PanelBloques;
import turing.ui.componentes.PanelCasos;
import turing.ui.componentes.PanelPatron;
import turing.ui.componentes.PanelSimulacion;
import turing.ui.componentes.PanelTabla;

/**
 * Implementación JavaFX de la interfaz. Arma la ventana (encabezado con el selector
 * de regla y una pestaña por funcionalidad) y crea un presentador por pestaña.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class InterfazJavaFXImpl implements Interfaz {

    private static final Logger LOG = LogManager.getLogger(InterfazJavaFXImpl.class);

    private CoordinadorApp coordinador;

    // Los controles se crean en construir(): JavaFX todavía no está iniciado cuando
    // el coordinador instancia esta clase.
    private ComboBox<Integer> selectorRegla;
    private Label subtitulo;
    private TabPane pestanias;

    private PanelTabla panelTabla;
    private PresentadorSimulacion presentadorSimulacion;
    private PresentadorPatron presentadorPatron;

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
        ReglaLocal regla = coordinador.crearRegla(Constantes.REGLA_POR_DEFECTO);
        selectorRegla = new ComboBox<>();
        subtitulo = new Label();
        pestanias = new TabPane();

        PanelSimulacion panelSimulacion = new PanelSimulacion();
        PanelPatron panelPatron = new PanelPatron();
        PanelBloques panelBloques = new PanelBloques();
        PanelCasos panelCasos = new PanelCasos();
        panelTabla = new PanelTabla();

        Tab tabSimulacion = new Tab("Simulación", panelSimulacion);
        pestanias.getTabs().addAll(
                tabSimulacion,
                new Tab("Patrón triangular", panelPatron),
                new Tab("Máquina (diagrama y δ)", panelTabla),
                new Tab("Bloques de construcción", panelBloques),
                new Tab("Casos de prueba", panelCasos));
        pestanias.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        presentadorSimulacion = new PresentadorSimulacion(panelSimulacion, coordinador, regla);
        presentadorPatron = new PresentadorPatron(panelPatron, coordinador, regla);
        new PresentadorBloques(panelBloques, coordinador);
        new PresentadorCasos(panelCasos, coordinador, cinta -> {
            selectorRegla.setValue(Constantes.REGLA_POR_DEFECTO);
            presentadorSimulacion.cargarTexto(cinta);
            pestanias.getSelectionModel().select(tabSimulacion);
        });
        mostrarRegla(regla);

        BorderPane raiz = new BorderPane(pestanias);
        raiz.setTop(construirEncabezado());
        raiz.getStyleClass().add("raiz");

        Scene escena = new Scene(raiz, 1360, 860);
        escena.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
        stage.setTitle("Máquina de Turing · Autómata celular elemental");
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

        Label etiqueta = new Label("Regla");
        etiqueta.getStyleClass().add("etiqueta");
        selectorRegla.getItems().setAll(coordinador.getReglasDisponibles());
        selectorRegla.setValue(Constantes.REGLA_POR_DEFECTO);
        selectorRegla.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer numero) {
                return numero == null ? "" : "Regla " + numero
                        + (numero == Constantes.REGLA_POR_DEFECTO ? "  (TP)" : "");
            }

            @Override
            public Integer fromString(String texto) {
                return Integer.parseInt(texto.replaceAll("\\D", ""));
            }
        });
        selectorRegla.setOnAction(evento -> cambiarRegla(coordinador.crearRegla(selectorRegla.getValue())));

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox result = new HBox(14, textos, espacio, etiqueta, selectorRegla);
        result.setAlignment(Pos.CENTER_LEFT);
        result.setPadding(new Insets(14, 20, 14, 20));
        result.getStyleClass().add("encabezado");
        return result;
    }

    private void cambiarRegla(ReglaLocal regla) {
        LOG.info("Regla seleccionada: {}", regla.getNombre());
        presentadorSimulacion.setRegla(regla);
        presentadorPatron.setRegla(regla);
        mostrarRegla(regla);
    }

    private void mostrarRegla(ReglaLocal regla) {
        panelTabla.mostrar(coordinador.obtenerDiagrama(regla), coordinador.obtenerMaquina(regla));
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
