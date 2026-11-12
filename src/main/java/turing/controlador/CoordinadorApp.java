package turing.controlador;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import turing.aplicacion.Constantes;
import turing.conexion.Factory;
import turing.dao.CasoPruebaDAO;
import turing.modelo.Alfabeto;
import turing.modelo.CasoPrueba;
import turing.modelo.CintaInfinita;
import turing.modelo.MaquinaTuring;
import turing.negocio.automata.EntradaAutomata;
import turing.negocio.automata.EvolucionAutomata;
import turing.negocio.automata.SimuladorDirecto;
import turing.negocio.diagrama.CompiladorDiagrama;
import turing.negocio.diagrama.DiagramaBloques;
import turing.negocio.motor.MotorTuring;
import turing.negocio.motor.MotorTuringSecuencial;
import turing.negocio.regla.ReglaElemental;
import turing.negocio.regla.ReglaLocal;
import turing.servicio.CasoPruebaService;
import turing.servicio.CasoPruebaServiceImpl;
import turing.servicio.MaquinaService;
import turing.servicio.MaquinaServiceImpl;
import turing.servicio.ResultadoCaso;
import turing.ui.Interfaz;
import turing.ui.InterfazJavaFXImpl;

/**
 * Coordinador central de la aplicación - 100% puro, sin lógica de UI.
 * El "Gerente" que ensambla dependencias e inicia la aplicación.
 *
 * Responsabilidades:
 * - Ensamblar los servicios con sus DAOs (usando Factory)
 * - Crear la vista e inyectarle las dependencias
 * - Iniciar la aplicación
 * - Ofrecer a la vista las operaciones de negocio
 *
 * NO maneja:
 * - UI/Interfaz gráfica
 * - JavaFX Application.launch
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class CoordinadorApp {

    private static final Logger LOG = LogManager.getLogger(CoordinadorApp.class);

    // Servicios de negocio
    private MaquinaService maquinaService;
    private CasoPruebaService casoPruebaService;

    // Vista
    private Interfaz interfaz;

    /**
     * Constructor del coordinador.
     */
    public CoordinadorApp() {
        LOG.info("Máquina de Turing - Autómata celular");
    }

    /**
     * Inicia la aplicación.
     * Ensambla los servicios, crea la vista e inicia JavaFX.
     */
    public void iniciarAplicacion() {
        inicializarServicios();
        crearVista();
        LOG.info("Iniciando interfaz gráfica");
        interfaz.iniciar();
    }

    /**
     * Ensambla los servicios sin crear la vista (lo usan las herramientas de consola).
     */
    public void inicializarServicios() {
        LOG.info("Inicializando servicios y DAOs...");
        Alfabeto alfabeto = new Alfabeto(Constantes.ALFABETO, Constantes.BLANCO);
        CasoPruebaDAO casoPruebaDAO = Factory.getInstancia("CASO_PRUEBA", CasoPruebaDAO.class);

        this.maquinaService = new MaquinaServiceImpl(alfabeto, new CompiladorDiagrama(), MotorTuringSecuencial::new);
        this.casoPruebaService = new CasoPruebaServiceImpl(casoPruebaDAO, maquinaService);
    }

    /**
     * Crea la vista e inyecta las dependencias.
     */
    private void crearVista() {
        LOG.info("Creando vista...");
        this.interfaz = new InterfazJavaFXImpl();
        this.interfaz.setCoordinador(this);
        LOG.info("Vista creada correctamente");
    }

    // ================= Operaciones para la vista =================

    /**
     * @param numero número de Wolfram
     * @return la regla correspondiente
     */
    public ReglaLocal crearRegla(int numero) {
        return new ReglaElemental(numero);
    }

    /**
     * @return números de regla ofrecidos en la interfaz
     */
    public List<Integer> getReglasDisponibles() {
        return Arrays.stream(Constantes.REGLAS_DISPONIBLES).boxed().toList();
    }

    public MaquinaTuring obtenerMaquina(ReglaLocal regla) {
        return maquinaService.obtenerMaquina(regla);
    }

    public DiagramaBloques obtenerDiagrama(ReglaLocal regla) {
        return maquinaService.obtenerDiagrama(regla);
    }

    public MaquinaTuring compilarBloque(String notacion) {
        return maquinaService.compilarBloque(notacion);
    }

    public MotorTuring nuevoMotor() {
        return maquinaService.nuevoMotor();
    }

    /**
     * Crea un motor con la máquina cargada sobre una cinta nueva.
     *
     * @param maquina  máquina a ejecutar
     * @param cinta    contenido inicial de la cinta (desde la posición 0)
     * @param posicion posición inicial de la cabeza
     * @return el motor listo para ejecutar
     */
    public MotorTuring prepararEjecucion(MaquinaTuring maquina, String cinta, long posicion) {
        MotorTuring result = maquinaService.nuevoMotor();
        result.cargar(maquina, new CintaInfinita(cinta, maquina.getAlfabeto().getBlanco(), posicion));
        return result;
    }

    /**
     * Simulación directa (sin máquina de Turing), usada para verificar resultados.
     *
     * @param entrada datos del autómata
     * @param regla   regla a aplicar
     * @return la evolución esperada
     */
    public EvolucionAutomata simularDirecto(EntradaAutomata entrada, ReglaLocal regla) {
        return new SimuladorDirecto(regla).simular(entrada);
    }

    public List<CasoPrueba> getCasosPrueba() {
        return casoPruebaService.buscarTodos();
    }

    public ResultadoCaso ejecutarCaso(CasoPrueba caso, ReglaLocal regla) {
        return casoPruebaService.ejecutar(caso, regla);
    }

    // ================= Getters para servicios =================

    public MaquinaService getMaquinaService() {
        return maquinaService;
    }

    public CasoPruebaService getCasoPruebaService() {
        return casoPruebaService;
    }
}
