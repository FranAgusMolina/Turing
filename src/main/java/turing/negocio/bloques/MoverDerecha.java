package turing.negocio.bloques;

import turing.modelo.Movimiento;

/**
 * Bloque R: se mueve un lugar a la derecha sin importar qué símbolo lea de la cinta.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class MoverDerecha extends BloqueMovimiento {

    public MoverDerecha() {
        super(Movimiento.DERECHA);
    }

    @Override
    public String getNotacion() {
        return "R";
    }

    @Override
    public String getDescripcion() {
        return "Se mueve un lugar a la derecha sin importar qué símbolo lea de la cinta.";
    }
}
