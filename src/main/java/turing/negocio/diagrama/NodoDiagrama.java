package turing.negocio.diagrama;

import turing.negocio.bloques.BloqueTuring;

/**
 * Nodo del diagrama: una instancia de un bloque de construcción.
 *
 * @param id          identificador único dentro del diagrama
 * @param bloque      bloque que ejecuta el nodo
 * @param comentario  explicación de para qué está el nodo en el algoritmo
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public record NodoDiagrama(String id, BloqueTuring bloque, String comentario) {

    /**
     * @return etiqueta con la que se documentan los estados que genera el nodo
     */
    public String etiqueta() {
        return id + " · " + bloque.getNotacion();
    }
}
