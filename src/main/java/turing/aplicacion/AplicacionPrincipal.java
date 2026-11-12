package turing.aplicacion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import turing.controlador.CoordinadorApp;

/**
 * Clase principal de la aplicación - Punto de entrada PURO (sin JavaFX).
 *
 * Responsabilidad única: Crear el CoordinadorApp e iniciar la aplicación.
 * NO extiende Application de JavaFX - implementa el patrón "Static Bridge".
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class AplicacionPrincipal {

    private static final Logger LOG = LogManager.getLogger(AplicacionPrincipal.class);

    public static void main(String[] args) {
        try {
            LOG.info("Iniciando aplicación...");
            CoordinadorApp coordinador = new CoordinadorApp();
            coordinador.iniciarAplicacion();
        } catch (Exception e) {
            LOG.fatal("Error crítico durante la inicialización de la aplicación. La aplicación se cerrará.", e);
            System.exit(1);
        }
    }
}
