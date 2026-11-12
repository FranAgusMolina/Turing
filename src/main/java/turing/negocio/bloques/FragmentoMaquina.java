package turing.negocio.bloques;

import java.util.List;

import turing.modelo.Estado;
import turing.modelo.Transicion;

/**
 * Resultado de compilar un bloque de construcción: un trozo de tabla δ con
 * un único estado de entrada y un único estado de salida.
 * <p>
 * Cuando la máquina llega al estado de salida el bloque terminó su trabajo; el
 * diagrama decide a qué bloque seguir mirando el símbolo que quedó bajo la cabeza.
 *
 * @param entrada      estado en el que comienza el bloque
 * @param salida       estado al que llega el bloque cuando termina
 * @param transiciones transiciones internas del bloque
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public record FragmentoMaquina(Estado entrada, Estado salida, List<Transicion> transiciones) {

    public FragmentoMaquina {
        transiciones = List.copyOf(transiciones);
    }
}
