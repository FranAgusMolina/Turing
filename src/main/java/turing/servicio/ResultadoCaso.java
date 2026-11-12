package turing.servicio;

import turing.modelo.CasoPrueba;

/**
 * Resultado de ejecutar un caso de prueba con la máquina de Turing.
 *
 * @param caso       caso ejecutado
 * @param obtenida   generación final que quedó en la cinta
 * @param directa    generación final según la simulación directa (oráculo)
 * @param pasos      cantidad de transiciones aplicadas
 * @param cintaFinal contenido completo de la cinta al detenerse
 * @param error      mensaje si la entrada era inválida o la máquina falló (vacío si no)
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public record ResultadoCaso(CasoPrueba caso, String obtenida, String directa, long pasos,
                            String cintaFinal, String error) {

    /** Valor esperado que indica que la entrada debe ser rechazada. */
    public static final String ESPERA_ERROR = "ERROR";

    /**
     * @return si el caso espera ERROR: true cuando la entrada fue rechazada.
     *         Si no: true cuando no hubo error, la MT coincide con la simulación directa y,
     *         si el caso trae un valor esperado, también coincide con él
     */
    public boolean correcto() {
        if (ESPERA_ERROR.equals(caso.esperada())) {
            return !error.isEmpty();
        }
        return error.isEmpty()
                && obtenida.equals(directa)
                && (!caso.tieneEsperada() || caso.esperada().equals(obtenida));
    }
}
