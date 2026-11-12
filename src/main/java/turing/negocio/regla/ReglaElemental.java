package turing.negocio.regla;

/**
 * Regla elemental de Wolfram. El número de la regla, escrito en binario con 8 bits,
 * es directamente su tabla de verdad: el bit k es el nuevo estado para el
 * vecindario cuyo valor binario es k (111 → bit 7, ..., 000 → bit 0).
 * <pre>
 *   Regla 110 = 0110 1110₂
 *   patrón:  111 110 101 100 011 010 001 000
 *   nuevo:    0   1   1   0   1   1   1   0
 * </pre>
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class ReglaElemental implements ReglaLocal {

    private final int numero;

    /**
     * @param numero número de Wolfram entre 0 y 255
     * @throws IllegalArgumentException si está fuera de rango
     */
    public ReglaElemental(int numero) {
        if (numero < 0 || numero > 255) {
            throw new IllegalArgumentException("El número de regla debe estar entre 0 y 255: " + numero);
        }
        this.numero = numero;
    }

    @Override
    public int siguiente(int izquierda, int centro, int derecha) {
        int patron = (izquierda << 2) | (centro << 1) | derecha;
        return (numero >> patron) & 1;
    }

    @Override
    public int getNumero() {
        return numero;
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
