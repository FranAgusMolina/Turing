package turing.ui.componentes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Vista de la pestaña "Bloques": laboratorio para probar cada bloque de construcción
 * (L, R, x, Rx, Lx, SR, SL) como máquina independiente y ver su tabla δ.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class PanelBloques extends SplitPane {

    private final ListView<String> catalogo = new ListView<>();
    private final TextField parametro = new TextField("#");
    private final Label titulo = new Label();
    private final Label descripcion = new Label();
    private final TextField campoCinta = new TextField();
    private final Spinner<Integer> posicion = new Spinner<>(-50, 200, 0);
    private final Button botonCargar = new Button("Cargar");
    private final Button botonPaso = new Button("Paso");
    private final Button botonEjecutar = new Button("Ejecutar");
    private final Label mensaje = new Label();
    private final VistaCinta vistaCinta = new VistaCinta();
    private final Label estado = new Label();
    private final TablaTransicionesVista tabla = new TablaTransicionesVista();

    public PanelBloques() {
        getStyleClass().add("panel");
        catalogo.setPrefWidth(220);
        catalogo.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(vacio || item == null ? null : item);
            }
        });
        catalogo.getStyleClass().add("catalogo");

        Label tituloCatalogo = new Label("Bloques de construcción");
        tituloCatalogo.getStyleClass().add("subtitulo");
        Label ayudaParametro = new Label("Símbolo x (o conjunto {a,b})");
        ayudaParametro.getStyleClass().add("etiqueta");
        parametro.getStyleClass().add("mono");
        VBox izquierda = new VBox(10, tituloCatalogo, catalogo, ayudaParametro, parametro);
        izquierda.setPadding(new Insets(16));
        VBox.setVgrow(catalogo, Priority.ALWAYS);

        titulo.getStyleClass().add("titulo-bloque");
        descripcion.getStyleClass().add("tenue");
        descripcion.setWrapText(true);
        campoCinta.getStyleClass().add("mono");
        campoCinta.setPromptText("Contenido de la cinta (usá _ para el blanco Δ)");
        HBox.setHgrow(campoCinta, Priority.ALWAYS);
        posicion.setEditable(true);
        posicion.setPrefWidth(90);
        botonCargar.getStyleClass().add("primario");
        Label etiquetaCinta = new Label("Cinta");
        etiquetaCinta.getStyleClass().add("etiqueta");
        Label etiquetaPosicion = new Label("Cabeza en");
        etiquetaPosicion.getStyleClass().add("etiqueta");
        HBox entrada = new HBox(10, etiquetaCinta, campoCinta, etiquetaPosicion, posicion,
                botonCargar, botonPaso, botonEjecutar);
        entrada.setAlignment(Pos.CENTER_LEFT);
        mensaje.getStyleClass().addAll("mensaje", "error");
        mensaje.setManaged(false);
        mensaje.setVisible(false);
        estado.getStyleClass().add("mono");

        VBox marcoCinta = new VBox(vistaCinta);
        marcoCinta.getStyleClass().add("tarjeta");
        VBox derecha = new VBox(12, titulo, descripcion, entrada, mensaje, marcoCinta, estado, tabla);
        derecha.setPadding(new Insets(16));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        getItems().addAll(izquierda, derecha);
        setDividerPositions(0.2);
    }

    /**
     * @param texto mensaje de error o null para ocultarlo
     */
    public void mostrarError(String texto) {
        boolean visible = texto != null && !texto.isEmpty();
        mensaje.setText(visible ? texto : "");
        mensaje.setManaged(visible);
        mensaje.setVisible(visible);
    }

    // ================= Accesos para el presentador =================

    public ListView<String> getCatalogo() { return catalogo; }
    public TextField getParametro() { return parametro; }
    public Label getTitulo() { return titulo; }
    public Label getDescripcion() { return descripcion; }
    public TextField getCampoCinta() { return campoCinta; }
    public Spinner<Integer> getPosicion() { return posicion; }
    public Button getBotonCargar() { return botonCargar; }
    public Button getBotonPaso() { return botonPaso; }
    public Button getBotonEjecutar() { return botonEjecutar; }
    public VistaCinta getVistaCinta() { return vistaCinta; }
    public Label getEstado() { return estado; }
    public TablaTransicionesVista getTabla() { return tabla; }
}
