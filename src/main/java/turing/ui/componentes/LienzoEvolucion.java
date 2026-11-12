package turing.ui.componentes;

import javafx.scene.canvas.GraphicsContext;

import turing.negocio.automata.EvolucionAutomata;

/**
 * Diagrama espacio-tiempo del autómata: cada fila es una generación (t crece hacia
 * abajo) y cada columna una celda. Es donde aparece el patrón triangular de la regla.
 * La generación que la máquina está calculando se pinta con el color de acento.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class LienzoEvolucion extends LienzoRedimensionable {

    private static final double MARGEN = 12;

    private EvolucionAutomata evolucion;
    private int columnas;
    private int filasTotales;

    public LienzoEvolucion() {
        setMinSize(120, 120);
    }

    /**
     * @param evolucion    generaciones a dibujar (null para vaciar)
     * @param columnas     N, cantidad de celdas por generación
     * @param filasTotales K + 1, cantidad de filas que tendrá el diagrama completo
     */
    public void mostrar(EvolucionAutomata evolucion, int columnas, int filasTotales) {
        this.evolucion = evolucion;
        this.columnas = Math.max(1, columnas);
        this.filasTotales = Math.max(1, filasTotales);
        redibujar();
    }

    @Override
    protected void dibujar(GraphicsContext contexto, double ancho, double alto) {
        if (evolucion == null) {
            return;
        }
        double lado = Math.max(1, Math.floor(Math.min(
                (ancho - 2 * MARGEN) / columnas,
                (alto - 2 * MARGEN) / filasTotales)));
        double origenX = Math.floor((ancho - lado * columnas) / 2);
        double origenY = MARGEN;
        double hueco = lado >= 6 ? 1 : 0;

        contexto.setFill(Paleta.MUERTA);
        contexto.fillRect(origenX, origenY, lado * columnas, lado * filasTotales);
        contexto.setFill(Paleta.VIVA);
        for (int aRow = 0; aRow < evolucion.cantidad(); aRow++) {
            dibujarFila(contexto, evolucion.generacion(aRow), origenX, origenY + aRow * lado, lado, hueco);
        }
        int[] parcial = evolucion.getParcial();
        if (parcial.length > 0 && evolucion.cantidad() < filasTotales) {
            double y = origenY + evolucion.cantidad() * lado;
            contexto.setFill(Paleta.ACENTO);
            dibujarFila(contexto, parcial, origenX, y, lado, hueco);
            contexto.setStroke(Paleta.ACENTO);
            contexto.setLineWidth(1);
            contexto.strokeRect(origenX + parcial.length * lado + 0.5, y + 0.5,
                    Math.max(1, lado - 1), Math.max(1, lado - 1));
        }
    }

    private void dibujarFila(GraphicsContext contexto, int[] fila, double x, double y, double lado, double hueco) {
        for (int aColumn = 0; aColumn < fila.length && aColumn < columnas; aColumn++) {
            if (fila[aColumn] == 1) {
                contexto.fillRect(x + aColumn * lado, y, lado - hueco, lado - hueco);
            }
        }
    }
}
