package turing.modelo;

import java.util.Arrays;

/**
 * Cinta infinita hacia ambos lados implementada sobre un arreglo que crece a demanda.
 * La posición lógica 0 corresponde al primer carácter de la entrada.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class CintaInfinita implements Cinta {

    private static final int CAPACIDAD_INICIAL = 64;

    private final char blanco;
    private char[] celdas;
    /** Índice del arreglo que corresponde a la posición lógica 0. */
    private int origen;
    private long cabeza;

    /**
     * Crea una cinta con la entrada escrita a partir de la posición 0 y la cabeza
     * ubicada sobre el primer carácter (convención de la cátedra).
     *
     * @param entrada contenido inicial
     * @param blanco  símbolo blanco
     */
    public CintaInfinita(String entrada, char blanco) {
        this.blanco = blanco;
        int capacidad = Math.max(CAPACIDAD_INICIAL, entrada.length() * 2 + 16);
        this.celdas = new char[capacidad];
        Arrays.fill(celdas, blanco);
        this.origen = capacidad / 4;
        entrada.getChars(0, entrada.length(), celdas, origen);
        this.cabeza = 0;
    }

    /**
     * Igual que {@link #CintaInfinita(String, char)} pero con la cabeza en otra posición.
     *
     * @param entrada  contenido inicial
     * @param blanco   símbolo blanco
     * @param posicion posición inicial de la cabeza
     */
    public CintaInfinita(String entrada, char blanco, long posicion) {
        this(entrada, blanco);
        this.cabeza = posicion;
        asegurarCapacidad(posicion);
    }

    @Override
    public char leer() {
        return simboloEn(cabeza);
    }

    @Override
    public void escribir(char simbolo) {
        asegurarCapacidad(cabeza);
        celdas[indice(cabeza)] = simbolo;
    }

    @Override
    public void mover(Movimiento movimiento) {
        cabeza += movimiento.getDesplazamiento();
        asegurarCapacidad(cabeza);
    }

    @Override
    public long getPosicion() {
        return cabeza;
    }

    @Override
    public char simboloEn(long posicion) {
        long anIndex = posicion + origen;
        if (anIndex < 0 || anIndex >= celdas.length) {
            return blanco;
        }
        return celdas[(int) anIndex];
    }

    @Override
    public long getPrimeraNoBlanca() {
        for (int anIndex = 0; anIndex < celdas.length; anIndex++) {
            if (celdas[anIndex] != blanco) {
                return anIndex - (long) origen;
            }
        }
        return cabeza;
    }

    @Override
    public long getUltimaNoBlanca() {
        for (int anIndex = celdas.length - 1; anIndex >= 0; anIndex--) {
            if (celdas[anIndex] != blanco) {
                return anIndex - (long) origen;
            }
        }
        return cabeza;
    }

    @Override
    public String contenido() {
        long primera = getPrimeraNoBlanca();
        long ultima = getUltimaNoBlanca();
        if (simboloEn(primera) == blanco) {
            return "";
        }
        return new String(celdas, indice(primera), (int) (ultima - primera + 1));
    }

    @Override
    public char getBlanco() {
        return blanco;
    }

    private int indice(long posicion) {
        return (int) (posicion + origen);
    }

    /**
     * Duplica el arreglo hacia el lado necesario cuando la cabeza sale de los límites.
     */
    private void asegurarCapacidad(long posicion) {
        while (posicion + origen < 0) {
            char[] nuevas = new char[celdas.length * 2];
            Arrays.fill(nuevas, blanco);
            System.arraycopy(celdas, 0, nuevas, celdas.length, celdas.length);
            origen += celdas.length;
            celdas = nuevas;
        }
        while (posicion + origen >= celdas.length) {
            celdas = Arrays.copyOf(celdas, celdas.length * 2);
            Arrays.fill(celdas, celdas.length / 2, celdas.length, blanco);
        }
    }
}
