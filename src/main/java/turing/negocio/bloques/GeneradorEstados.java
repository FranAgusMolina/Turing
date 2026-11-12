package turing.negocio.bloques;

import turing.modelo.Estado;

/**
 * Genera estados con nombres consecutivos (q0, q1, q2, ...).
 * Un único generador se comparte entre todos los bloques de un diagrama para que
 * los nombres de estado nunca se repitan dentro de la máquina compilada.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class GeneradorEstados {

    private final String prefijo;
    private int siguiente;

    /**
     * Crea un generador con prefijo "q".
     */
    public GeneradorEstados() {
        this("q");
    }

    /**
     * @param prefijo prefijo de los nombres de estado
     */
    public GeneradorEstados(String prefijo) {
        this.prefijo = prefijo;
        this.siguiente = 0;
    }

    /**
     * @param bloque etiqueta del bloque al que pertenecerá el estado
     * @return un estado nuevo, nunca antes generado
     */
    public Estado nuevo(String bloque) {
        return new Estado(prefijo + siguiente++, bloque);
    }

    /**
     * @param nombre nombre fijo (por ejemplo "qH" para el estado de parada)
     * @param bloque etiqueta del bloque
     * @return un estado con el nombre indicado
     */
    public Estado conNombre(String nombre, String bloque) {
        return new Estado(nombre, bloque);
    }
}
