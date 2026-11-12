package turing.ui;

import turing.controlador.CoordinadorApp;

/**
 * Contrato común para todas las implementaciones de UI.
 * Define el mínimo comportamiento necesario para que el Coordinador pueda lanzarla.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public interface Interfaz {

    /**
     * El Coordinador se inyecta a sí mismo en la UI para permitir la comunicación de vuelta.
     *
     * @param coordinador La instancia del coordinador principal
     */
    void setCoordinador(CoordinadorApp coordinador);

    /**
     * Punto de entrada para iniciar la interfaz de usuario.
     * En JavaFX: launch() o mostrar un Stage.
     */
    void iniciar();
}
