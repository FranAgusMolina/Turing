package turing.modelo;

/**
 * Contrato de la cinta infinita sobre la que opera la cabeza lecto-escritora.
 * Las posiciones son enteros (pueden ser negativas): la cinta es infinita hacia ambos lados.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public interface Cinta {

    /**
     * @return el símbolo bajo la cabeza
     */
    char leer();

    /**
     * Escribe un símbolo en la celda bajo la cabeza.
     *
     * @param simbolo símbolo a escribir
     */
    void escribir(char simbolo);

    /**
     * Mueve la cabeza una celda (o ninguna).
     *
     * @param movimiento dirección del movimiento
     */
    void mover(Movimiento movimiento);

    /**
     * @return posición actual de la cabeza
     */
    long getPosicion();

    /**
     * @param posicion posición absoluta
     * @return el símbolo en esa posición (blanco si nunca se escribió)
     */
    char simboloEn(long posicion);

    /**
     * @return posición de la primera celda no blanca (o la de la cabeza si la cinta está vacía)
     */
    long getPrimeraNoBlanca();

    /**
     * @return posición de la última celda no blanca (o la de la cabeza si la cinta está vacía)
     */
    long getUltimaNoBlanca();

    /**
     * @return el contenido entre la primera y la última celda no blanca
     */
    String contenido();

    /**
     * @return el símbolo blanco de esta cinta
     */
    char getBlanco();
}
