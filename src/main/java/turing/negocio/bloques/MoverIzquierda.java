package turing.negocio.bloques;

import turing.modelo.Movimiento;

/**
 * Bloque L: se mueve un lugar a la izquierda sin importar qué símbolo lea de la cinta.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class MoverIzquierda extends BloqueMovimiento {

    public MoverIzquierda() {
        super(Movimiento.IZQUIERDA);
    }

    @Override
    public String getNotacion() {
        return "L";
    }

    @Override
    public String getDescripcion() {
        return "Se mueve un lugar a la izquierda sin importar qué símbolo lea de la cinta.";
    }
}
