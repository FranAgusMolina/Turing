package turing.negocio.bloques;

import turing.modelo.Movimiento;

/**
 * Bloque SL: corre un lugar a la izquierda la cadena de caracteres que se encuentra a la
 * derecha desde la posición actual hasta el primer espacio en blanco.
 * <pre>
 *   Δ [a] b c Δ   →   a b c [Δ] Δ
 * </pre>
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class DesplazarIzquierda extends BloqueDesplazamiento {

    public DesplazarIzquierda() {
        super(Movimiento.IZQUIERDA);
    }

    @Override
    public String getNotacion() {
        return "SL";
    }

    @Override
    public String getDescripcion() {
        return "Corre un lugar a la izquierda la cadena que está a la derecha, desde la posición "
                + "actual hasta el primer blanco. La cabeza queda en la celda que se libera.";
    }
}
