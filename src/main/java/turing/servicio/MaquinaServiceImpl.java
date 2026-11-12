package turing.servicio;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import turing.modelo.Alfabeto;
import turing.modelo.MaquinaTuring;
import turing.negocio.automata.DiagramaAutomataCelular;
import turing.negocio.diagrama.CompiladorDiagrama;
import turing.negocio.diagrama.DiagramaBloques;
import turing.negocio.motor.MotorTuring;
import turing.negocio.regla.ReglaLocal;

/**
 * Implementación del servicio de máquinas: compila los diagramas y cachea el resultado.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class MaquinaServiceImpl implements MaquinaService {

    private static final Logger LOG = LogManager.getLogger(MaquinaServiceImpl.class);

    private final Alfabeto alfabeto;
    private final CompiladorDiagrama compilador;
    private final Supplier<MotorTuring> fabricaMotor;
    private final Map<Integer, MaquinaTuring> cache = new ConcurrentHashMap<>();

    /**
     * Constructor con inyección de dependencias.
     *
     * @param alfabeto     alfabeto de trabajo Σ
     * @param compilador   compilador de diagramas
     * @param fabricaMotor proveedor de motores
     */
    public MaquinaServiceImpl(Alfabeto alfabeto, CompiladorDiagrama compilador,
                              Supplier<MotorTuring> fabricaMotor) {
        this.alfabeto = alfabeto;
        this.compilador = compilador;
        this.fabricaMotor = fabricaMotor;
    }

    @Override
    public DiagramaBloques obtenerDiagrama(ReglaLocal regla) {
        return DiagramaAutomataCelular.crear(regla);
    }

    @Override
    public MaquinaTuring obtenerMaquina(ReglaLocal regla) {
        return cache.computeIfAbsent(regla.getNumero(), numero -> {
            MaquinaTuring result = compilador.compilar(obtenerDiagrama(regla), alfabeto);
            LOG.info("Compilada {}: {} estados, {} transiciones",
                    result.getNombre(), result.getEstados().size(), result.getTransiciones().size());
            return result;
        });
    }

    @Override
    public MaquinaTuring compilarBloque(String notacion) {
        DiagramaBloques diagrama = new DiagramaBloques("Bloque " + notacion)
                .nodo("B", notacion, "Bloque bajo prueba")
                .arco("B", DiagramaBloques.CUALQUIERA, DiagramaBloques.ALTO);
        return compilador.compilar(diagrama, alfabeto);
    }

    @Override
    public MotorTuring nuevoMotor() {
        return fabricaMotor.get();
    }
}
