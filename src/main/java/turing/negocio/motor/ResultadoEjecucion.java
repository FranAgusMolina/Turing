package turing.negocio.motor;

/**
 * Situación en la que se encuentra una ejecución.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public enum ResultadoEjecucion {

    /** Todavía hay una transición definida para el estado y símbolo actuales. */
    EN_EJECUCION("En ejecución"),

    /** La máquina llegó a un estado final: parada normal. */
    DETENIDA_EN_ESTADO_FINAL("Detenida en estado final"),

    /** δ no está definida para el par actual y el estado no es final: parada anómala. */
    DETENIDA_SIN_TRANSICION("Detenida sin transición (anómala)"),

    /** Se alcanzó el límite de pasos permitido. */
    LIMITE_ALCANZADO("Límite de pasos alcanzado");

    private final String descripcion;

    ResultadoEjecucion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @return true si la máquina ya no puede avanzar
     */
    public boolean estaDetenida() {
        return this != EN_EJECUCION;
    }
}
