package turing.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Lanzador de JavaFX - Implementa el patrón "Static Bridge".
 *
 * Este es el ÚNICO lugar donde se extiende Application.
 * Actúa como puente entre el código puro y JavaFX.
 *
 * Responsabilidades:
 * - Recibir la referencia estática de la interfaz
 * - Lanzar JavaFX (Application.launch)
 * - Entregar el Stage principal a la interfaz
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class JavaFXLauncher extends Application {

    private static final Logger LOG = LogManager.getLogger(JavaFXLauncher.class);

    // Referencia estática para el puente entre código puro y JavaFX
    private static InterfazJavaFXImpl staticInterfaz;

    /**
     * Método estático que recibe la interfaz y lanza JavaFX.
     *
     * @param interfaz la implementación de la interfaz
     */
    public static void launchJavaFX(InterfazJavaFXImpl interfaz) {
        LOG.info("Iniciando JavaFX...");
        JavaFXLauncher.staticInterfaz = interfaz;
        Application.launch(JavaFXLauncher.class);
    }

    /**
     * Método start de JavaFX - "Humble Launcher".
     * Solo delega el trabajo a la instancia de InterfazJavaFXImpl.
     *
     * @param primaryStage el Stage principal de JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            if (staticInterfaz == null) {
                throw new IllegalStateException("La interfaz no fue configurada antes de lanzar JavaFX");
            }
            staticInterfaz.construir(primaryStage);
        } catch (Exception e) {
            LOG.error("Error crítico durante inicialización", e);
            Platform.exit();
            throw new RuntimeException("Fallo en inicialización de JavaFX", e);
        }
    }
}
