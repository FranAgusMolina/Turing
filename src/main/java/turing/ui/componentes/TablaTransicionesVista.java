package turing.ui.componentes;

import java.util.function.Function;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import turing.modelo.MaquinaTuring;
import turing.modelo.Transicion;

/**
 * Tabla δ de una máquina de Turing con filtro por estado o bloque.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class TablaTransicionesVista extends VBox {

    private final TableView<Transicion> tabla = new TableView<>();
    private final TextField filtro = new TextField();
    private final Label resumen = new Label();
    private FilteredList<Transicion> filas = new FilteredList<>(FXCollections.observableArrayList());

    public TablaTransicionesVista() {
        setSpacing(8);
        filtro.setPromptText("Filtrar por estado, símbolo o bloque (ej. q12, SIG_CELDA, R#)");
        filtro.textProperty().addListener((obs, viejo, nuevo) -> aplicarFiltro());
        HBox.setHgrow(filtro, Priority.ALWAYS);
        resumen.getStyleClass().add("tenue");

        tabla.getColumns().add(columna("Estado", 70, t -> t.origen().nombre()));
        tabla.getColumns().add(columna("Lee", 50, t -> String.valueOf(t.simboloLeido())));
        tabla.getColumns().add(columna("Nuevo estado", 100, t -> t.destino().nombre()));
        tabla.getColumns().add(columna("Escribe", 65, t -> String.valueOf(t.simboloEscrito())));
        tabla.getColumns().add(columna("Mov", 50, t -> String.valueOf(t.movimiento().getNotacion())));
        tabla.getColumns().add(columna("Bloque", 220, t -> t.origen().bloque()));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setPlaceholder(new Label("Sin transiciones"));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        getChildren().addAll(new HBox(10, filtro), resumen, tabla);
    }

    private TableColumn<Transicion, String> columna(String titulo, double ancho, Function<Transicion, String> valor) {
        TableColumn<Transicion, String> result = new TableColumn<>(titulo);
        result.setPrefWidth(ancho);
        result.setCellValueFactory(celda -> new SimpleStringProperty(valor.apply(celda.getValue())));
        result.setSortable(false);
        return result;
    }

    /**
     * @param maquina máquina cuya δ se muestra
     */
    public void mostrar(MaquinaTuring maquina) {
        filas = new FilteredList<>(FXCollections.observableArrayList(maquina.getTransiciones()));
        tabla.setItems(filas);
        resumen.setText(String.format("|S| = %d estados   ·   Σ = %s   ·   s0 = %s   ·   F = %s   ·   |δ| = %d transiciones",
                maquina.getEstados().size(), maquina.getAlfabeto(), maquina.getEstadoInicial(),
                maquina.getEstadosFinales(), maquina.getTransiciones().size()));
        aplicarFiltro();
    }

    /**
     * Selecciona y muestra una transición (la última aplicada por el motor).
     *
     * @param transicion transición a resaltar
     */
    public void resaltar(Transicion transicion) {
        if (transicion == null) {
            tabla.getSelectionModel().clearSelection();
            return;
        }
        int indice = filas.indexOf(transicion);
        if (indice >= 0) {
            tabla.getSelectionModel().clearAndSelect(indice);
            tabla.scrollTo(Math.max(0, indice - 3));
        }
    }

    private void aplicarFiltro() {
        String texto = filtro.getText() == null ? "" : filtro.getText().trim().toLowerCase();
        filas.setPredicate(t -> texto.isEmpty()
                || t.origen().nombre().toLowerCase().equals(texto)
                || t.destino().nombre().toLowerCase().equals(texto)
                || t.origen().bloque().toLowerCase().contains(texto)
                || (texto.length() == 1 && t.simboloLeido() == texto.charAt(0)));
    }
}
