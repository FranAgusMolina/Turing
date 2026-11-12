package turing.negocio.motor;

import turing.aplicacion.Constantes;
import turing.modelo.Cinta;
import turing.modelo.Estado;
import turing.modelo.MaquinaTuring;
import turing.modelo.Transicion;

/**
 * Motor determinista que ejecuta una transición por paso:
 * lee el símbolo, consulta δ, escribe, mueve la cabeza y cambia de estado.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class MotorTuringSecuencial implements MotorTuring {

    private final long limitePasos;

    private MaquinaTuring maquina;
    private Cinta cinta;
    private Estado estadoActual;
    private long pasos;
    private Transicion ultimaTransicion;
    private ResultadoEjecucion resultado;

    /**
     * Crea un motor con el límite de pasos por defecto.
     */
    public MotorTuringSecuencial() {
        this(Constantes.LIMITE_PASOS);
    }

    /**
     * @param limitePasos cantidad total de pasos tras la cual se aborta la ejecución
     */
    public MotorTuringSecuencial(long limitePasos) {
        this.limitePasos = limitePasos;
    }

    @Override
    public void cargar(MaquinaTuring maquina, Cinta cinta) {
        this.maquina = maquina;
        this.cinta = cinta;
        this.estadoActual = maquina.getEstadoInicial();
        this.pasos = 0;
        this.ultimaTransicion = null;
        this.resultado = calcularResultado();
    }

    @Override
    public Transicion paso() {
        if (resultado.estaDetenida()) {
            return null;
        }
        Transicion aTransition = maquina.transicion(estadoActual, cinta.leer());
        cinta.escribir(aTransition.simboloEscrito());
        cinta.mover(aTransition.movimiento());
        estadoActual = aTransition.destino();
        ultimaTransicion = aTransition;
        pasos++;
        resultado = calcularResultado();
        return aTransition;
    }

    @Override
    public ResultadoEjecucion ejecutar(long maximoPasos) {
        long realizados = 0;
        while (!resultado.estaDetenida() && realizados < maximoPasos) {
            paso();
            realizados++;
        }
        return resultado;
    }

    private ResultadoEjecucion calcularResultado() {
        if (maquina.esFinal(estadoActual)) {
            return ResultadoEjecucion.DETENIDA_EN_ESTADO_FINAL;
        }
        if (maquina.transicion(estadoActual, cinta.leer()) == null) {
            return ResultadoEjecucion.DETENIDA_SIN_TRANSICION;
        }
        if (pasos >= limitePasos) {
            return ResultadoEjecucion.LIMITE_ALCANZADO;
        }
        return ResultadoEjecucion.EN_EJECUCION;
    }

    @Override
    public ResultadoEjecucion getResultado() {
        return resultado;
    }

    @Override
    public Estado getEstadoActual() {
        return estadoActual;
    }

    @Override
    public Cinta getCinta() {
        return cinta;
    }

    @Override
    public MaquinaTuring getMaquina() {
        return maquina;
    }

    @Override
    public long getPasos() {
        return pasos;
    }

    @Override
    public Transicion getUltimaTransicion() {
        return ultimaTransicion;
    }
}
