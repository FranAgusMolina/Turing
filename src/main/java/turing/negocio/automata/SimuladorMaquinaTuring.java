package turing.negocio.automata;

import java.util.function.Supplier;

import turing.modelo.CintaInfinita;
import turing.modelo.MaquinaTuring;
import turing.negocio.motor.MotorTuring;
import turing.negocio.motor.ResultadoEjecucion;

/**
 * Calcula la evolución ejecutando la máquina de Turing del autómata y leyendo
 * las generaciones que quedaron registradas en la cinta.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class SimuladorMaquinaTuring implements SimuladorAutomata {

    private final MaquinaTuring maquina;
    private final Supplier<MotorTuring> fabricaMotor;

    /**
     * @param maquina      máquina compilada del autómata
     * @param fabricaMotor proveedor de motores (inyección de dependencias)
     */
    public SimuladorMaquinaTuring(MaquinaTuring maquina, Supplier<MotorTuring> fabricaMotor) {
        this.maquina = maquina;
        this.fabricaMotor = fabricaMotor;
    }

    @Override
    public EvolucionAutomata simular(EntradaAutomata entrada) {
        MotorTuring motor = ejecutar(entrada);
        return CodificadorCinta.leerEvolucion(motor.getCinta().contenido());
    }

    /**
     * Ejecuta la máquina hasta que se detenga.
     *
     * @param entrada datos del autómata
     * @return el motor en su configuración final
     * @throws IllegalStateException si la máquina no terminó en su estado final
     */
    public MotorTuring ejecutar(EntradaAutomata entrada) {
        MotorTuring result = fabricaMotor.get();
        String contenido = CodificadorCinta.codificar(entrada);
        result.cargar(maquina, new CintaInfinita(contenido, maquina.getAlfabeto().getBlanco()));
        ResultadoEjecucion resultado = result.ejecutar(Long.MAX_VALUE);
        if (resultado != ResultadoEjecucion.DETENIDA_EN_ESTADO_FINAL) {
            throw new IllegalStateException("La máquina no terminó correctamente: "
                    + resultado.getDescripcion() + " en " + result.getEstadoActual());
        }
        return result;
    }
}
