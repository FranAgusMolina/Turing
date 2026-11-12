package turing.ui.componentes;

import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import turing.modelo.MaquinaTuring;
import turing.negocio.diagrama.DiagramaBloques;
import turing.negocio.diagrama.NodoDiagrama;

/**
 * Vista de la pestaña "Máquina": el diagrama de bloques con el que se diseñó la
 * máquina y la tabla δ que resulta de compilarlo.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class PanelTabla extends VBox {

    private final Label explicacion = new Label();
    private final TableView<NodoDiagrama> nodos = new TableView<>();
    private final TablaTransicionesVista transiciones = new TablaTransicionesVista();
    private DiagramaBloques diagrama;

    public PanelTabla() {
        setSpacing(12);
        setPadding(new Insets(16));
        getStyleClass().add("panel");

        explicacion.setWrapText(true);
        explicacion.getStyleClass().add("tenue");

        nodos.getColumns().add(columna("Nodo", 170, NodoDiagrama::id));
        nodos.getColumns().add(columna("Bloque", 90, nodo -> nodo.bloque().getNotacion()));
        nodos.getColumns().add(columna("Flechas (símbolo → nodo)", 330, this::flechas));
        nodos.getColumns().add(columna("Función en el algoritmo", 420, NodoDiagrama::comentario));
        nodos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        nodos.getStyleClass().add("mono-tabla");

        Tab tabDiagrama = new Tab("Diagrama de bloques", nodos);
        Tab tabDelta = new Tab("Tabla de transiciones δ", transiciones);
        transiciones.setPadding(new Insets(10, 0, 0, 0));
        TabPane pestanias = new TabPane(tabDiagrama, tabDelta);
        pestanias.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        pestanias.getStyleClass().add("sub-pestanias");
        VBox.setVgrow(pestanias, Priority.ALWAYS);

        getChildren().addAll(explicacion, pestanias);
    }

    private TableColumn<NodoDiagrama, String> columna(String titulo, double ancho,
                                                      Function<NodoDiagrama, String> valor) {
        TableColumn<NodoDiagrama, String> result = new TableColumn<>(titulo);
        result.setPrefWidth(ancho);
        result.setSortable(false);
        result.setCellValueFactory(celda -> new SimpleStringProperty(valor.apply(celda.getValue())));
        return result;
    }

    private String flechas(NodoDiagrama nodo) {
        return diagrama.arcosDesde(nodo.id()).stream()
                .map(anArc -> anArc.condicion() + " → " + anArc.destino())
                .collect(Collectors.joining("   "));
    }

    /**
     * @param diagrama diagrama de bloques de la máquina
     * @param maquina  máquina compilada a partir del diagrama
     */
    public void mostrar(DiagramaBloques diagrama, MaquinaTuring maquina) {
        this.diagrama = diagrama;
        nodos.getItems().setAll(diagrama.getNodos());
        transiciones.mostrar(maquina);
        explicacion.setText(String.format(
                "%s. La máquina se diseñó como un diagrama de %d bloques (R, L, x, Rx, Lx) unidos por "
                + "flechas etiquetadas con el símbolo leído. Cada bloque se compila a sus transiciones y cada "
                + "flecha a una transición sin movimiento (N), obteniendo una única tabla δ con %d estados y "
                + "%d transiciones. El nodo inicial es %s y el estado de parada es qH.",
                diagrama.getNombre(), diagrama.getNodos().size(), maquina.getEstados().size(),
                maquina.getTransiciones().size(), diagrama.getInicial().id()));
    }
}
