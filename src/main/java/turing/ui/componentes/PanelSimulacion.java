package turing.ui.componentes;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Vista de la pestaña "Simulación": cinta, controles de ejecución, diagrama
 * espacio-tiempo que se va formando y tabla δ con la transición aplicada.
 * Sólo construye controles; el comportamiento está en PresentadorSimulacion.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class PanelSimulacion extends VBox {

    private final TextField campoCinta = new TextField();
    private final ComboBox<String> ejemplos = new ComboBox<>();
    private final Button botonCargar = new Button("Cargar cinta");
    private final Label mensaje = new Label();

    private final VistaCinta vistaCinta = new VistaCinta();

    private final Label valorEstado = valor();
    private final Label valorBloque = valor();
    private final Label valorPasos = valor();
    private final Label valorContador = valor();
    private final Label valorGeneracion = valor();
    private final Label valorTransicion = valor();
    private final Label valorResultado = valor();

    private final Button botonReiniciar = new Button("⟲ Reiniciar");
    private final Button botonPaso = new Button("Paso");
    private final ToggleButton botonReproducir = new ToggleButton("▶ Reproducir");
    private final Button botonGeneracion = new Button("Siguiente generación");
    private final Button botonFinal = new Button("Hasta el final");
    private final Slider velocidad = new Slider(0, 6, 1.3);
    private final Label valorVelocidad = new Label();

    private final LienzoEvolucion lienzo = new LienzoEvolucion();
    private final TextArea textoGeneraciones = new TextArea();
    private final TablaTransicionesVista tablaTransiciones = new TablaTransicionesVista();

    public PanelSimulacion() {
        setSpacing(12);
        setPadding(new Insets(16));
        getStyleClass().add("panel");

        SplitPane inferior = new SplitPane(construirEvolucion(), tablaTransiciones);
        inferior.setDividerPositions(0.56);
        VBox.setVgrow(inferior, Priority.ALWAYS);

        getChildren().addAll(construirEntrada(), mensaje, tarjeta(vistaCinta), construirInformacion(),
                construirControles(), inferior);
        mensaje.getStyleClass().add("mensaje");
        mensaje.setManaged(false);
        mensaje.setVisible(false);
    }

    private HBox construirEntrada() {
        Label etiqueta = new Label("Cinta");
        etiqueta.getStyleClass().add("etiqueta");
        campoCinta.setPromptText("K#celdas#   ej. 100#000000010000000#");
        campoCinta.getStyleClass().add("mono");
        HBox.setHgrow(campoCinta, Priority.ALWAYS);
        ejemplos.setPromptText("Ejemplos");
        ejemplos.setPrefWidth(250);
        botonCargar.getStyleClass().add("primario");
        botonCargar.setDefaultButton(true);
        HBox result = new HBox(10, etiqueta, campoCinta, ejemplos, botonCargar);
        result.setAlignment(Pos.CENTER_LEFT);
        return result;
    }

    private GridPane construirInformacion() {
        GridPane result = new GridPane();
        result.setHgap(28);
        result.setVgap(4);
        String[] titulos = {"Estado", "Bloque", "Pasos", "Contador K", "Generación", "Última δ", "Resultado"};
        Label[] valores = {valorEstado, valorBloque, valorPasos, valorContador, valorGeneracion,
                valorTransicion, valorResultado};
        for (int anIndex = 0; anIndex < titulos.length; anIndex++) {
            Label titulo = new Label(titulos[anIndex].toUpperCase());
            titulo.getStyleClass().add("titulo-dato");
            result.add(titulo, anIndex, 0);
            result.add(valores[anIndex], anIndex, 1);
        }
        return result;
    }

    private HBox construirControles() {
        botonReproducir.getStyleClass().add("primario");
        Label etiquetaVelocidad = new Label("Velocidad");
        etiquetaVelocidad.getStyleClass().add("etiqueta");
        velocidad.setPrefWidth(200);
        valorVelocidad.setMinWidth(110);
        valorVelocidad.getStyleClass().add("mono");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox result = new HBox(8, botonReiniciar, botonPaso, botonReproducir, botonGeneracion, botonFinal,
                espacio, etiquetaVelocidad, velocidad, valorVelocidad);
        result.setAlignment(Pos.CENTER_LEFT);
        return result;
    }

    private VBox construirEvolucion() {
        Label titulo = new Label("Diagrama espacio-tiempo (leído de la cinta)");
        titulo.getStyleClass().add("subtitulo");
        textoGeneraciones.setEditable(false);
        textoGeneraciones.getStyleClass().add("mono");
        textoGeneraciones.setPrefRowCount(5);
        SplitPane partes = new SplitPane(lienzo, textoGeneraciones);
        partes.setOrientation(Orientation.VERTICAL);
        partes.setDividerPositions(0.72);
        VBox.setVgrow(partes, Priority.ALWAYS);
        VBox result = new VBox(8, titulo, partes);
        result.setPadding(new Insets(0, 10, 0, 0));
        return result;
    }

    private static VBox tarjeta(Region contenido) {
        VBox result = new VBox(contenido);
        result.getStyleClass().add("tarjeta");
        return result;
    }

    private static Label valor() {
        Label result = new Label("–");
        result.getStyleClass().add("valor-dato");
        return result;
    }

    /**
     * Muestra u oculta un mensaje (error de validación, aviso, etc.).
     *
     * @param texto texto del mensaje, null o vacío para ocultarlo
     * @param error true para estilo de error
     */
    public void mostrarMensaje(String texto, boolean error) {
        boolean visible = texto != null && !texto.isEmpty();
        mensaje.setText(visible ? texto : "");
        mensaje.getStyleClass().removeAll("error", "aviso");
        mensaje.getStyleClass().add(error ? "error" : "aviso");
        mensaje.setManaged(visible);
        mensaje.setVisible(visible);
    }

    // ================= Accesos para el presentador =================

    public TextField getCampoCinta() { return campoCinta; }
    public ComboBox<String> getEjemplos() { return ejemplos; }
    public Button getBotonCargar() { return botonCargar; }
    public VistaCinta getVistaCinta() { return vistaCinta; }
    public Label getValorEstado() { return valorEstado; }
    public Label getValorBloque() { return valorBloque; }
    public Label getValorPasos() { return valorPasos; }
    public Label getValorContador() { return valorContador; }
    public Label getValorGeneracion() { return valorGeneracion; }
    public Label getValorTransicion() { return valorTransicion; }
    public Label getValorResultado() { return valorResultado; }
    public Button getBotonReiniciar() { return botonReiniciar; }
    public Button getBotonPaso() { return botonPaso; }
    public ToggleButton getBotonReproducir() { return botonReproducir; }
    public Button getBotonGeneracion() { return botonGeneracion; }
    public Button getBotonFinal() { return botonFinal; }
    public Slider getVelocidad() { return velocidad; }
    public Label getValorVelocidad() { return valorVelocidad; }
    public LienzoEvolucion getLienzo() { return lienzo; }
    public TextArea getTextoGeneraciones() { return textoGeneraciones; }
    public TablaTransicionesVista getTablaTransiciones() { return tablaTransiciones; }
}
