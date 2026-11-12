package turing.aplicacion;

/**
 * Constantes globales de la aplicación.
 * Centraliza los símbolos del alfabeto de trabajo, los valores por defecto
 * de la simulación y los límites de ejecución.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public final class Constantes {

    // ================= Alfabeto de trabajo (Σ) =================

    /** Símbolo blanco que llena la cinta infinita (Δ). */
    public static final char BLANCO = 'Δ';

    /** Delimitador entre el contador K y cada generación. */
    public static final char SEPARADOR = '#';

    /** Celda muerta. */
    public static final char CERO = '0';

    /** Celda viva. */
    public static final char UNO = '1';

    /** Celda muerta ya procesada (marca temporal de la máquina). */
    public static final char CERO_MARCADO = 'o';

    /** Celda viva ya procesada (marca temporal de la máquina). */
    public static final char UNO_MARCADO = 'i';

    /** Alfabeto completo en el orden en que se muestra en la tabla δ. */
    public static final String ALFABETO = "01#oiΔ";

    // ================= Simulación =================

    /** Regla del autómata pedida en el enunciado del TP. */
    public static final int REGLA_POR_DEFECTO = 110;

    /** Longitud mínima N de la hilera de celdas exigida por el enunciado. */
    public static final int LONGITUD_MINIMA = 8;

    /** Cantidad máxima de pasos que se ejecutan antes de abortar (protección ante bucles). */
    public static final long LIMITE_PASOS = 200_000_000L;

    /** Reglas que se ofrecen en la interfaz además de la regla por defecto. */
    public static final int[] REGLAS_DISPONIBLES = {110, 101, 30, 90, 54, 150, 184};

    private Constantes() {
        throw new AssertionError("No instanciable");
    }
}
