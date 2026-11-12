package turing.servicio;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import turing.dao.CasoPruebaDAO;
import turing.modelo.CasoPrueba;
import turing.negocio.automata.CodificadorCinta;
import turing.negocio.automata.EntradaAutomata;
import turing.negocio.automata.EvolucionAutomata;
import turing.negocio.automata.SimuladorDirecto;
import turing.negocio.automata.SimuladorMaquinaTuring;
import turing.negocio.automata.ValidadorEntrada;
import turing.negocio.motor.MotorTuring;
import turing.negocio.regla.ReglaLocal;

/**
 * Implementación del servicio de casos de prueba.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class CasoPruebaServiceImpl implements CasoPruebaService {

    private static final Logger LOG = LogManager.getLogger(CasoPruebaServiceImpl.class);

    private final CasoPruebaDAO casoPruebaDAO;
    private final MaquinaService maquinaService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param casoPruebaDAO  origen de los casos
     * @param maquinaService servicio que provee las máquinas
     */
    public CasoPruebaServiceImpl(CasoPruebaDAO casoPruebaDAO, MaquinaService maquinaService) {
        this.casoPruebaDAO = casoPruebaDAO;
        this.maquinaService = maquinaService;
    }

    @Override
    public List<CasoPrueba> buscarTodos() {
        return casoPruebaDAO.buscarTodos();
    }

    @Override
    public ResultadoCaso ejecutar(CasoPrueba caso, ReglaLocal regla) {
        String error = ValidadorEntrada.validar(caso.entrada()).orElse("");
        if (!error.isEmpty()) {
            return new ResultadoCaso(caso, "", "", 0, "", error);
        }
        try {
            EntradaAutomata entrada = CodificadorCinta.decodificar(caso.entrada());
            SimuladorMaquinaTuring simulador = new SimuladorMaquinaTuring(
                    maquinaService.obtenerMaquina(regla), maquinaService::nuevoMotor);
            MotorTuring motor = simulador.ejecutar(entrada);
            String cintaFinal = motor.getCinta().contenido();
            EvolucionAutomata evolucion = CodificadorCinta.leerEvolucion(cintaFinal);
            EvolucionAutomata directa = new SimuladorDirecto(regla).simular(entrada);
            return new ResultadoCaso(caso,
                    CodificadorCinta.celdasATexto(evolucion.ultima()),
                    CodificadorCinta.celdasATexto(directa.ultima()),
                    motor.getPasos(), cintaFinal, "");
        } catch (RuntimeException e) {
            LOG.error("Error ejecutando el caso {}", caso.numero(), e);
            return new ResultadoCaso(caso, "", "", 0, "", e.getMessage());
        }
    }
}
