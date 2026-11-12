package turing.ui.componentes;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import turing.aplicacion.Constantes;
import turing.modelo.Cinta;

/**
 * Dibuja la cinta infinita centrada en la cabeza lecto-escritora, con el estado
 * actual de la unidad de control sobre la cabeza.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class VistaCinta extends LienzoRedimensionable {

    private static final double LADO = 34;
    private static final double SEPARACION = 3;
    private static final Font FUENTE_SIMBOLO = Font.font("Monospaced", FontWeight.BOLD, 17);
    private static final Font FUENTE_INDICE = Font.font("Monospaced", 9);
    private static final Font FUENTE_ESTADO = Font.font("Monospaced", FontWeight.BOLD, 13);

    private Cinta cinta;
    private String estado = "";

    public VistaCinta() {
        setMinHeight(118);
        setPrefHeight(118);
    }

    /**
     * @param cinta  cinta a mostrar (null para vaciar)
     * @param estado nombre del estado actual
     */
    public void mostrar(Cinta cinta, String estado) {
        this.cinta = cinta;
        this.estado = estado == null ? "" : estado;
        redibujar();
    }

    @Override
    protected void dibujar(GraphicsContext contexto, double ancho, double alto) {
        if (cinta == null) {
            return;
        }
        double paso = LADO + SEPARACION;
        double centroX = ancho / 2;
        double y = 40;
        int visibles = (int) Math.ceil(ancho / paso) / 2 + 1;
        long cabeza = cinta.getPosicion();

        contexto.setTextAlign(TextAlignment.CENTER);
        contexto.setTextBaseline(VPos.CENTER);
        for (long aPosition = cabeza - visibles; aPosition <= cabeza + visibles; aPosition++) {
            double x = centroX + (aPosition - cabeza) * paso - LADO / 2;
            dibujarCelda(contexto, x, y, cinta.simboloEn(aPosition), aPosition == cabeza);
            contexto.setFont(FUENTE_INDICE);
            contexto.setFill(Paleta.TEXTO_TENUE);
            contexto.fillText(String.valueOf(aPosition), x + LADO / 2, y + LADO + 10);
        }
        dibujarCabeza(contexto, centroX, y);
    }

    private void dibujarCelda(GraphicsContext contexto, double x, double y, char simbolo, boolean bajoCabeza) {
        contexto.setFill(fondoDe(simbolo));
        contexto.fillRoundRect(x, y, LADO, LADO, 6, 6);
        contexto.setStroke(bajoCabeza ? Paleta.ACENTO : Paleta.BORDE);
        contexto.setLineWidth(bajoCabeza ? 2.5 : 1);
        contexto.strokeRoundRect(x, y, LADO, LADO, 6, 6);
        contexto.setFont(FUENTE_SIMBOLO);
        contexto.setFill(colorDe(simbolo));
        contexto.fillText(String.valueOf(simbolo), x + LADO / 2, y + LADO / 2 + 1);
    }

    private void dibujarCabeza(GraphicsContext contexto, double centroX, double y) {
        contexto.setFill(Paleta.ACENTO);
        contexto.fillPolygon(
                new double[]{centroX - 7, centroX + 7, centroX},
                new double[]{y - 13, y - 13, y - 4}, 3);
        contexto.setFont(FUENTE_ESTADO);
        contexto.fillText(estado, centroX, y - 24);
    }

    private Color fondoDe(char simbolo) {
        return simbolo == Constantes.UNO || simbolo == Constantes.UNO_MARCADO
                ? Paleta.VIVA.deriveColor(0, 1, 1, 0.14)
                : Paleta.SUPERFICIE;
    }

    private Color colorDe(char simbolo) {
        return switch (simbolo) {
            case Constantes.SEPARADOR -> Paleta.SEPARADOR;
            case Constantes.CERO_MARCADO, Constantes.UNO_MARCADO -> Paleta.MARCADA;
            case Constantes.BLANCO -> Paleta.TEXTO_TENUE.deriveColor(0, 1, 1, 0.45);
            default -> Paleta.TEXTO;
        };
    }
}
