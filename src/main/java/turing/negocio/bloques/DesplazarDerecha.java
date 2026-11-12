package turing.negocio.bloques;

import turing.modelo.Movimiento;

/**
 * Bloque SR: corre un lugar a la derecha la cadena de caracteres que se encuentra a la
 * izquierda desde la posición actual hasta el primer espacio en blanco.
 * <pre>
 *   Δ a b [c] Δ   →   Δ [Δ] a b c
 * </pre>
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class DesplazarDerecha extends BloqueDesplazamiento {

    public DesplazarDerecha() {
        super(Movimiento.DERECHA);
    }

    @Override
    public String getNotacion() {
        return "SR";
    }

    @Override
    public String getDescripcion() {
        return "Corre un lugar a la derecha la cadena que está a la izquierda, desde la posición "
                + "actual hasta el primer blanco. La cabeza queda en la celda que se libera.";
    }
}
