package turing.negocio.automata;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import turing.aplicacion.Constantes;
import turing.modelo.Alfabeto;
import turing.modelo.MaquinaTuring;
import turing.negocio.diagrama.CompiladorDiagrama;
import turing.negocio.motor.MotorTuring;
import turing.negocio.motor.MotorTuringSecuencial;
import turing.negocio.regla.ReglaElemental;
import turing.negocio.regla.ReglaLocal;

/**
 * Verifica la máquina de Turing del autómata contra el simulador directo y
 * contra los ejemplos del enunciado.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
class MaquinaAutomataTest {

    private static final Alfabeto SIGMA = new Alfabeto(Constantes.ALFABETO, Constantes.BLANCO);

    private MaquinaTuring compilar(ReglaLocal regla) {
        return new CompiladorDiagrama().compilar(DiagramaAutomataCelular.crear(regla), SIGMA);
    }

    private SimuladorMaquinaTuring simuladorTuring(int numeroRegla) {
        return new SimuladorMaquinaTuring(compilar(new ReglaElemental(numeroRegla)), MotorTuringSecuencial::new);
    }

    @Test
    void tablaDeVerdadDeLaRegla110() {
        ReglaLocal regla = new ReglaElemental(110);
        int[][] patrones = {{1, 1, 1}, {1, 1, 0}, {1, 0, 1}, {1, 0, 0}, {0, 1, 1}, {0, 1, 0}, {0, 0, 1}, {0, 0, 0}};
        int[] esperado = {0, 1, 1, 0, 1, 1, 1, 0};
        for (int anIndex = 0; anIndex < patrones.length; anIndex++) {
            int[] p = patrones[anIndex];
            assertEquals(esperado[anIndex], regla.siguiente(p[0], p[1], p[2]), "patrón " + anIndex);
        }
    }

    @Test
    void ejemploDelEnunciadoConKIgualA3() {
        EntradaAutomata entrada = new EntradaAutomata(3, new int[]{0, 0, 0, 1, 0, 0});
        EvolucionAutomata evolucion = simuladorTuring(110).simular(entrada);
        assertEquals(4, evolucion.cantidad());
        assertArrayEquals(new int[]{0, 0, 1, 1, 0, 0}, evolucion.generacion(1));
        assertArrayEquals(new int[]{0, 1, 1, 1, 0, 0}, evolucion.generacion(2));
        assertArrayEquals(new int[]{1, 1, 0, 1, 0, 0}, evolucion.generacion(3));
    }

    @Test
    void cintaFinalRegistraTodasLasGeneracionesYElContadorEnCero() {
        EntradaAutomata entrada = CodificadorCinta.decodificar("11#00010000#");
        MotorTuring motor = simuladorTuring(110).ejecutar(entrada);
        assertEquals("00#00010000#00110000#01110000#11010000#", motor.getCinta().contenido());
        assertEquals('#', motor.getCinta().leer());
    }

    @Test
    void kIgualACeroNoCalculaNada() {
        EntradaAutomata entrada = CodificadorCinta.decodificar("0#10110010#");
        MotorTuring motor = simuladorTuring(110).ejecutar(entrada);
        assertEquals("0#10110010#", motor.getCinta().contenido());
    }

    @Test
    void fronterasCiclicas() {
        // celda 0: vecindario (1,1,1) sólo si su izquierda es la última celda → 0
        EntradaAutomata izquierdaCiclica = new EntradaAutomata(1, new int[]{1, 1, 0, 0, 0, 0, 0, 1});
        assertArrayEquals(new int[]{0, 1, 0, 0, 0, 0, 1, 1},
                simuladorTuring(110).simular(izquierdaCiclica).ultima());
        // celda 7: vecindario (1,0,1) sólo si su derecha es la primera celda → 1
        EntradaAutomata derechaCiclica = new EntradaAutomata(1, new int[]{1, 0, 0, 0, 0, 0, 1, 0});
        assertArrayEquals(new int[]{1, 0, 0, 0, 0, 1, 1, 1},
                simuladorTuring(110).simular(derechaCiclica).ultima());
    }

    @Test
    void ejemplosDelEnunciadoCoincidenConLaSimulacionDirecta() {
        for (String aTape : new String[]{"100#000000010000000#", "1110#01101001#"}) {
            EntradaAutomata entrada = CodificadorCinta.decodificar(aTape);
            EvolucionAutomata esperada = new SimuladorDirecto(new ReglaElemental(110)).simular(entrada);
            EvolucionAutomata obtenida = simuladorTuring(110).simular(entrada);
            assertTrue(esperada.mismasGeneraciones(obtenida), aTape);
            assertEquals(entrada.generaciones() + 1, obtenida.cantidad());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {110, 101, 30, 90, 54, 184, 0, 255})
    void maquinaCoincideConSimulacionDirectaEnEntradasAleatorias(int numeroRegla) {
        Random random = new Random(numeroRegla);
        SimuladorAutomata directo = new SimuladorDirecto(new ReglaElemental(numeroRegla));
        SimuladorAutomata turing = simuladorTuring(numeroRegla);
        for (int aCase = 0; aCase < 15; aCase++) {
            int[] celdas = random.ints(1 + random.nextInt(20), 0, 2).toArray();
            EntradaAutomata entrada = new EntradaAutomata(random.nextInt(8), celdas);
            assertTrue(directo.simular(entrada).mismasGeneraciones(turing.simular(entrada)), entrada.toString());
        }
    }

    @Test
    void laMaquinaEsDeterministaYUsaSoloElAlfabeto() {
        MaquinaTuring maquina = compilar(new ReglaElemental(110));
        assertTrue(maquina.getTransiciones().stream()
                .allMatch(aTransition -> SIGMA.contiene(aTransition.simboloEscrito())));
        assertEquals(1, maquina.getEstadosFinales().size());
    }
}
