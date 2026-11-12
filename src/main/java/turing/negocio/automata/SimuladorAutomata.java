package turing.negocio.automata;

/**
 * Estrategia para calcular la evolución de un autómata celular.
 * Hay dos implementaciones intercambiables (LSP):
 * <ul>
 *   <li>{@link SimuladorDirecto}: aplica la regla con un bucle; es el oráculo de las pruebas.</li>
 *   <li>{@link SimuladorMaquinaTuring}: ejecuta la máquina de Turing y lee el resultado de la cinta.</li>
 * </ul>
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public interface SimuladorAutomata {

    /**
     * @param entrada configuración inicial y cantidad de generaciones
     * @return las K+1 generaciones (la inicial y las K calculadas)
     */
    EvolucionAutomata simular(EntradaAutomata entrada);
}
