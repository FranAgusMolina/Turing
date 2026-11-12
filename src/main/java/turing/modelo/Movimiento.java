package turing.modelo;

/**
 * Movimientos posibles de la cabeza lecto-escritora.
 * Se respeta la notación de la cátedra: δ : S × Σ → S × Σ × {I, D, N}.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public enum Movimiento {

    /** Movimiento a izquierda. */
    IZQUIERDA('I', -1),

    /** Movimiento a derecha. */
    DERECHA('D', 1),

    /** Sin movimiento. */
    NINGUNO('N', 0);

    private final char notacion;
    private final int desplazamiento;

    Movimiento(char notacion, int desplazamiento) {
        this.notacion = notacion;
        this.desplazamiento = desplazamiento;
    }

    /**
     * @return letra con la que se escribe el movimiento en la tabla δ (I, D o N)
     */
    public char getNotacion() {
        return notacion;
    }

    /**
     * @return -1, +1 o 0 según la dirección
     */
    public int getDesplazamiento() {
        return desplazamiento;
    }

    /**
     * Devuelve el movimiento en sentido contrario.
     * Se usa para construir bloques simétricos (L/R, Lx/Rx, SL/SR).
     *
     * @return el movimiento opuesto; NINGUNO es su propio opuesto
     */
    public Movimiento opuesto() {
        return switch (this) {
            case IZQUIERDA -> DERECHA;
            case DERECHA -> IZQUIERDA;
            case NINGUNO -> NINGUNO;
        };
    }
}
