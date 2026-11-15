package turing.negocio.regla;

/**
 * Regla 110 del autómata celular elemental, la que pide el enunciado.
 * <pre>
 *   patrón:  111 110 101 100 011 010 001 000
 *   nuevo:    0   1   1   0   1   1   1   0
 * </pre>
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class Regla110 implements ReglaLocal {

    /** Nuevo estado indexado por el vecindario leído como binario (l·4 + c·2 + r). */
    private static final int[] TABLA = {
            0, // 000
            1, // 001
            1, // 010
            1, // 011
            0, // 100
            1, // 101
            1, // 110
            0  // 111
    };

    @Override
    public int siguiente(int izquierda, int centro, int derecha) {
        return TABLA[(izquierda << 2) | (centro << 1) | derecha];
    }

    @Override
    public String getNombre() {
        return "Regla 110";
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
