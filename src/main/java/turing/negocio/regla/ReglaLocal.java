package turing.negocio.regla;

/**
 * Función de transición local de un autómata celular unidimensional de radio 1:
 * dado el vecindario (izquierda, centro, derecha) devuelve el nuevo valor de la celda central.
 * La única implementación es {@link Regla110}.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public interface ReglaLocal {

    /**
     * @param izquierda valor de la celda izquierda (0 o 1)
     * @param centro    valor de la celda central (0 o 1)
     * @param derecha   valor de la celda derecha (0 o 1)
     * @return el valor de la celda central en la generación siguiente (0 o 1)
     */
    int siguiente(int izquierda, int centro, int derecha);

    /**
     * @return nombre legible, por ejemplo "Regla 110"
     */
    String getNombre();
}
