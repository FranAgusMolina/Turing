package turing.negocio.motor;

import turing.modelo.Cinta;
import turing.modelo.Estado;
import turing.modelo.MaquinaTuring;
import turing.modelo.Transicion;

/**
 * Contrato del motor que ejecuta una máquina de Turing paso a paso sobre una cinta.
 * La máquina es sólo una descripción (S, Σ, δ, s0, F); el motor mantiene la
 * configuración instantánea: estado actual, cinta y posición de la cabeza.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public interface MotorTuring {

    /**
     * Prepara una nueva ejecución desde el estado inicial.
     *
     * @param maquina máquina a ejecutar
     * @param cinta   cinta con la entrada y la cabeza ya posicionada
     */
    void cargar(MaquinaTuring maquina, Cinta cinta);

    /**
     * Aplica una transición.
     *
     * @return la transición aplicada, o null si la máquina está detenida
     */
    Transicion paso();

    /**
     * Ejecuta hasta detenerse o hasta agotar la cantidad de pasos indicada.
     *
     * @param maximoPasos cantidad máxima de pasos a ejecutar en esta llamada
     * @return el resultado al terminar la llamada
     */
    ResultadoEjecucion ejecutar(long maximoPasos);

    /**
     * @return el resultado actual de la ejecución
     */
    ResultadoEjecucion getResultado();

    /**
     * @return el estado actual de la unidad de control
     */
    Estado getEstadoActual();

    /**
     * @return la cinta sobre la que se ejecuta
     */
    Cinta getCinta();

    /**
     * @return la máquina cargada
     */
    MaquinaTuring getMaquina();

    /**
     * @return la cantidad de pasos ejecutados desde la carga
     */
    long getPasos();

    /**
     * @return la última transición aplicada o null si no se aplicó ninguna
     */
    Transicion getUltimaTransicion();
}
