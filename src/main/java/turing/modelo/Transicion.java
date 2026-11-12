package turing.modelo;

/**
 * Una entrada de la función de transición: δ(origen, leído) = (destino, escrito, movimiento).
 *
 * @param origen         estado actual
 * @param simboloLeido   símbolo bajo la cabeza
 * @param destino        estado siguiente
 * @param simboloEscrito símbolo que se escribe en la celda actual
 * @param movimiento     movimiento de la cabeza luego de escribir
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public record Transicion(Estado origen, char simboloLeido, Estado destino,
                         char simboloEscrito, Movimiento movimiento) {

    /**
     * @return la transición en la notación de la cátedra, por ejemplo δ(q0, 1) = (q1, 0, D)
     */
    @Override
    public String toString() {
        return "δ(" + origen + ", " + simboloLeido + ") = ("
                + destino + ", " + simboloEscrito + ", " + movimiento.getNotacion() + ")";
    }
}
