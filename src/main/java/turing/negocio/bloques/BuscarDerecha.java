package turing.negocio.bloques;

import java.util.Set;

import turing.modelo.Movimiento;

/**
 * Bloque Rx: se desplaza a la derecha hasta la primera x.
 * Hay uno por cada símbolo del alfabeto (o conjunto de símbolos).
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class BuscarDerecha extends BloqueBusqueda {

    /**
     * @param buscados símbolos que detienen la búsqueda
     */
    public BuscarDerecha(Set<Character> buscados) {
        super(Movimiento.DERECHA, buscados);
    }

    /**
     * @param buscado único símbolo x buscado
     */
    public BuscarDerecha(char buscado) {
        this(Set.of(buscado));
    }

    @Override
    protected String getLetra() {
        return "R";
    }

    @Override
    public String getDescripcion() {
        return "Se desplaza a la derecha hasta el primer símbolo de " + getBuscados() + ".";
    }
}
