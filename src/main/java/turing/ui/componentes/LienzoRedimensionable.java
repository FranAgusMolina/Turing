package turing.ui.componentes;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

/**
 * Canvas que ocupa todo el espacio de su contenedor y se redibuja al cambiar de tamaño.
 * Las subclases sólo implementan {@link #dibujar(GraphicsContext, double, double)}.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public abstract class LienzoRedimensionable extends Pane {

    private final Canvas canvas = new Canvas();

    protected LienzoRedimensionable() {
        getChildren().add(canvas);
        widthProperty().addListener((obs, viejo, nuevo) -> redibujar());
        heightProperty().addListener((obs, viejo, nuevo) -> redibujar());
    }

    @Override
    protected void layoutChildren() {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
    }

    /**
     * Vuelve a dibujar el contenido completo.
     */
    public final void redibujar() {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
        GraphicsContext contexto = canvas.getGraphicsContext2D();
        contexto.setFill(Paleta.FONDO);
        contexto.fillRect(0, 0, getWidth(), getHeight());
        if (getWidth() > 0 && getHeight() > 0) {
            dibujar(contexto, getWidth(), getHeight());
        }
    }

    /**
     * @param contexto contexto gráfico ya limpio
     * @param ancho    ancho disponible
     * @param alto     alto disponible
     */
    protected abstract void dibujar(GraphicsContext contexto, double ancho, double alto);
}
