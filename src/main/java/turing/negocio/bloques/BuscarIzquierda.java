package turing.negocio.bloques;

import java.util.Set;

import turing.modelo.Movimiento;

/**
 * Bloque Lx: se desplaza a la izquierda hasta la primera x.
 * Hay uno por cada símbolo del alfabeto (o conjunto de símbolos).
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class BuscarIzquierda extends BloqueBusqueda {

    /**
     * @param buscados símbolos que detienen la búsqueda
     */
    public BuscarIzquierda(Set<Character> buscados) {
        super(Movimiento.IZQUIERDA, buscados);
    }

    /**
     * @param buscado único símbolo x buscado
     */
    public BuscarIzquierda(char buscado) {
        this(Set.of(buscado));
    }

    @Override
    protected String getLetra() {
        return "L";
    }

    @Override
    public String getDescripcion() {
        return "Se desplaza a la izquierda hasta el primer símbolo de " + getBuscados() + ".";
    }
}
