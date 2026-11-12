package turing.negocio.automata;

import java.util.Arrays;

/**
 * Datos de entrada del autómata: configuración inicial y cantidad de generaciones K.
 *
 * @param generaciones K, cantidad de generaciones a calcular
 * @param celdas       configuración inicial (cada elemento 0 o 1)
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public record EntradaAutomata(int generaciones, int[] celdas) {

    public EntradaAutomata {
        celdas = celdas.clone();
    }

    @Override
    public int[] celdas() {
        return celdas.clone();
    }

    /**
     * @return N, la longitud de la hilera
     */
    public int longitud() {
        return celdas.length;
    }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof EntradaAutomata entrada
                && entrada.generaciones == generaciones
                && Arrays.equals(entrada.celdas, celdas);
    }

    @Override
    public int hashCode() {
        return 31 * generaciones + Arrays.hashCode(celdas);
    }

    @Override
    public String toString() {
        return "EntradaAutomata[K=" + generaciones + ", celdas=" + CodificadorCinta.celdasATexto(celdas) + "]";
    }
}
