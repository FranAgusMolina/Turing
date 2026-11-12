package turing.ui;

import java.util.function.Predicate;

import javafx.animation.AnimationTimer;

import turing.modelo.Transicion;
import turing.negocio.motor.MotorTuring;

/**
 * Hace avanzar un {@link MotorTuring} sobre el hilo de JavaFX a una velocidad dada
 * (pasos por segundo) y avisa a la vista después de cada cuadro.
 * Opcionalmente se detiene cuando una transición cumple una condición
 * (por ejemplo "se cerró una generación").
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class EjecutorAnimado {

    /** Máximo de pasos por cuadro para no congelar la interfaz. */
    private static final long PRESUPUESTO_POR_CUADRO = 400_000;

    private final Runnable alActualizar;
    private final Runnable alDetenerse;
    private final AnimationTimer temporizador;

    private MotorTuring motor;
    private double pasosPorSegundo = 20;
    private double acumulado;
    private long ultimoCuadro;
    private Predicate<Transicion> condicionParada;
    private boolean activo;

    /**
     * @param alActualizar se llama después de cada cuadro con pasos ejecutados
     * @param alDetenerse  se llama cuando la animación se detiene sola o por pausa
     */
    public EjecutorAnimado(Runnable alActualizar, Runnable alDetenerse) {
        this.alActualizar = alActualizar;
        this.alDetenerse = alDetenerse;
        this.temporizador = new AnimationTimer() {
            @Override
            public void handle(long ahora) {
                cuadro(ahora);
            }
        };
    }

    /**
     * @param motor motor a animar
     */
    public void setMotor(MotorTuring motor) {
        detener();
        this.motor = motor;
    }

    /**
     * @param pasosPorSegundo velocidad; {@link Double#POSITIVE_INFINITY} = lo más rápido posible
     */
    public void setPasosPorSegundo(double pasosPorSegundo) {
        this.pasosPorSegundo = pasosPorSegundo;
    }

    /**
     * Comienza a avanzar hasta que la máquina se detenga o se pause.
     *
     * @param condicionParada si no es null, se pausa al aplicar una transición que la cumpla
     */
    public void iniciar(Predicate<Transicion> condicionParada) {
        if (motor == null || motor.getResultado().estaDetenida()) {
            return;
        }
        this.condicionParada = condicionParada;
        this.acumulado = 1;
        this.ultimoCuadro = 0;
        this.activo = true;
        temporizador.start();
    }

    /**
     * Pausa la animación.
     */
    public void detener() {
        if (activo) {
            activo = false;
            temporizador.stop();
            alDetenerse.run();
        }
    }

    /**
     * @return true mientras la animación está corriendo
     */
    public boolean isActivo() {
        return activo;
    }

    private void cuadro(long ahora) {
        double segundos = ultimoCuadro == 0 ? 1.0 / 60 : (ahora - ultimoCuadro) / 1e9;
        ultimoCuadro = ahora;
        acumulado += Double.isInfinite(pasosPorSegundo) ? PRESUPUESTO_POR_CUADRO : pasosPorSegundo * segundos;
        long pasos = (long) Math.min(acumulado, PRESUPUESTO_POR_CUADRO);
        acumulado -= pasos;

        boolean cumplida = false;
        for (long aStep = 0; aStep < pasos && !motor.getResultado().estaDetenida(); aStep++) {
            Transicion aplicada = motor.paso();
            if (condicionParada != null && aplicada != null && condicionParada.test(aplicada)) {
                cumplida = true;
                break;
            }
        }
        alActualizar.run();
        if (cumplida || motor.getResultado().estaDetenida()) {
            detener();
        }
    }
}
