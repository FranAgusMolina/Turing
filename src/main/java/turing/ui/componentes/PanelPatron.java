package turing.ui.componentes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Vista de la pestaña "Patrón triangular": genera una evolución grande ejecutando la
 * máquina de Turing y dibuja el diagrama espacio-tiempo completo.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class PanelPatron extends VBox {

    /** Configuraciones iniciales que ofrece el combo. */
    public static final String INICIAL_DERECHA = "Una celda viva en el extremo derecho";
    public static final String INICIAL_CENTRO = "Una celda viva en el centro";
    public static final String INICIAL_ALEATORIA = "Aleatoria (50 %)";

    private final Spinner<Integer> longitud = new Spinner<>(8, 401, 101);
    private final Spinner<Integer> generaciones = new Spinner<>(1, 600, 100);
    private final ComboBox<String> inicial = new ComboBox<>();
    private final Button botonGenerar = new Button("Generar con la máquina de Turing");
    private final Button botonCancelar = new Button("Cancelar");
    private final ProgressBar progreso = new ProgressBar(0);
    private final Label estado = new Label("Elegí N y K y generá el patrón: cada celda la calcula la máquina de Turing.");
    private final Label verificacion = new Label();
    private final LienzoEvolucion lienzo = new LienzoEvolucion();

    public PanelPatron() {
        setSpacing(12);
        setPadding(new Insets(16));
        getStyleClass().add("panel");

        longitud.setEditable(true);
        longitud.setPrefWidth(95);
        generaciones.setEditable(true);
        generaciones.setPrefWidth(95);
        inicial.getItems().addAll(INICIAL_DERECHA, INICIAL_CENTRO, INICIAL_ALEATORIA);
        inicial.setValue(INICIAL_DERECHA);
        botonGenerar.getStyleClass().add("primario");
        botonCancelar.setDisable(true);
        progreso.setPrefWidth(220);
        estado.getStyleClass().add("tenue");
        verificacion.getStyleClass().add("verificacion");

        HBox controles = new HBox(10, etiqueta("N"), longitud, etiqueta("K"), generaciones,
                etiqueta("Inicial"), inicial, botonGenerar, botonCancelar);
        controles.setAlignment(Pos.CENTER_LEFT);
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox pie = new HBox(12, progreso, estado, espacio, verificacion);
        pie.setAlignment(Pos.CENTER_LEFT);

        VBox marco = new VBox(lienzo);
        marco.getStyleClass().add("tarjeta");
        VBox.setVgrow(lienzo, Priority.ALWAYS);
        VBox.setVgrow(marco, Priority.ALWAYS);

        getChildren().addAll(controles, pie, marco);
    }

    private static Label etiqueta(String texto) {
        Label result = new Label(texto);
        result.getStyleClass().add("etiqueta");
        return result;
    }

    // ================= Accesos para el presentador =================

    public Spinner<Integer> getLongitud() { return longitud; }
    public Spinner<Integer> getGeneraciones() { return generaciones; }
    public ComboBox<String> getInicial() { return inicial; }
    public Button getBotonGenerar() { return botonGenerar; }
    public Button getBotonCancelar() { return botonCancelar; }
    public ProgressBar getProgreso() { return progreso; }
    public Label getEstado() { return estado; }
    public Label getVerificacion() { return verificacion; }
    public LienzoEvolucion getLienzo() { return lienzo; }
}
