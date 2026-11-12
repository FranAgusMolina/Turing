package turing.ui.componentes;

import java.util.function.Function;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import turing.modelo.CasoPrueba;

/**
 * Vista de la pestaña "Casos de prueba": tabla con los casos del archivo y el
 * resultado de ejecutarlos en la máquina de Turing.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class PanelCasos extends VBox {

    /**
     * Fila de la tabla: un caso y su resultado (que se completa al ejecutarlo).
     */
    public static final class FilaCaso {
        private final CasoPrueba caso;
        private final StringProperty obtenida = new SimpleStringProperty("");
        private final StringProperty pasos = new SimpleStringProperty("");
        private final StringProperty veredicto = new SimpleStringProperty("pendiente");

        public FilaCaso(CasoPrueba caso) {
            this.caso = caso;
        }

        public CasoPrueba getCaso() { return caso; }
        public StringProperty obtenidaProperty() { return obtenida; }
        public StringProperty pasosProperty() { return pasos; }
        public StringProperty veredictoProperty() { return veredicto; }
    }

    private final TableView<FilaCaso> tabla = new TableView<>();
    private final Button botonEjecutar = new Button("Ejecutar todos");
    private final Button botonRecargar = new Button("Recargar archivo");
    private final Button botonAbrir = new Button("Abrir en simulación");
    private final Label resumen = new Label();

    public PanelCasos() {
        setSpacing(12);
        setPadding(new Insets(16));
        getStyleClass().add("panel");

        botonEjecutar.getStyleClass().add("primario");
        Label nota = new Label("casos_prueba.txt · un caso por línea · los valores esperados corresponden a la Regla 110");
        nota.getStyleClass().add("tenue");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        resumen.getStyleClass().add("verificacion");
        HBox barra = new HBox(10, botonEjecutar, botonRecargar, botonAbrir, nota, espacio, resumen);
        barra.setAlignment(Pos.CENTER_LEFT);

        tabla.getColumns().add(columnaFija("Línea", 55, fila -> String.valueOf(fila.getCaso().numero())));
        tabla.getColumns().add(columnaFija("Entrada", 260, fila -> fila.getCaso().entrada()));
        tabla.getColumns().add(columnaFija("Esperada", 210, fila -> fila.getCaso().esperada()));
        tabla.getColumns().add(columna("Obtenida (MT)", 210, FilaCaso::obtenidaProperty));
        tabla.getColumns().add(columna("Pasos", 95, FilaCaso::pasosProperty));
        tabla.getColumns().add(columna("Resultado", 110, FilaCaso::veredictoProperty));
        tabla.getColumns().add(columnaFija("Descripción", 360, fila -> fila.getCaso().descripcion()));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setRowFactory(vista -> new TableRow<>() {
            @Override
            protected void updateItem(FilaCaso fila, boolean vacia) {
                super.updateItem(fila, vacia);
                getStyleClass().removeAll("fila-ok", "fila-falla");
                if (!vacia && fila != null) {
                    String veredicto = fila.veredictoProperty().get();
                    if (veredicto.startsWith("✔")) {
                        getStyleClass().add("fila-ok");
                    } else if (veredicto.startsWith("✘")) {
                        getStyleClass().add("fila-falla");
                    }
                }
            }
        });
        tabla.getStyleClass().add("mono-tabla");
        VBox.setVgrow(tabla, Priority.ALWAYS);

        getChildren().addAll(barra, tabla);
    }

    private TableColumn<FilaCaso, String> columnaFija(String titulo, double ancho, Function<FilaCaso, String> valor) {
        TableColumn<FilaCaso, String> result = new TableColumn<>(titulo);
        result.setPrefWidth(ancho);
        result.setCellValueFactory(celda -> new SimpleStringProperty(valor.apply(celda.getValue())));
        return result;
    }

    private TableColumn<FilaCaso, String> columna(String titulo, double ancho,
                                                  Function<FilaCaso, StringProperty> propiedad) {
        TableColumn<FilaCaso, String> result = new TableColumn<>(titulo);
        result.setPrefWidth(ancho);
        result.setCellValueFactory(celda -> propiedad.apply(celda.getValue()));
        return result;
    }

    // ================= Accesos para el presentador =================

    public TableView<FilaCaso> getTabla() { return tabla; }
    public Button getBotonEjecutar() { return botonEjecutar; }
    public Button getBotonRecargar() { return botonRecargar; }
    public Button getBotonAbrir() { return botonAbrir; }
    public Label getResumen() { return resumen; }
}
