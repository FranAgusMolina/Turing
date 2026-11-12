package turing.negocio.automata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import turing.negocio.regla.ReglaLocal;

/**
 * Simulación directa del autómata con fronteras cíclicas.
 * No usa la máquina de Turing: sirve como referencia para verificarla.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class SimuladorDirecto implements SimuladorAutomata {

    private final ReglaLocal regla;

    /**
     * @param regla regla local a aplicar
     */
    public SimuladorDirecto(ReglaLocal regla) {
        this.regla = regla;
    }

    @Override
    public EvolucionAutomata simular(EntradaAutomata entrada) {
        List<int[]> result = new ArrayList<>();
        int[] actual = entrada.celdas();
        result.add(actual);
        for (int aGeneration = 0; aGeneration < entrada.generaciones(); aGeneration++) {
            actual = siguienteGeneracion(actual);
            result.add(actual);
        }
        return new EvolucionAutomata(result);
    }

    /**
     * @param celdas generación t
     * @return generación t+1
     */
    public int[] siguienteGeneracion(int[] celdas) {
        int n = celdas.length;
        return IntStream.range(0, n)
                .map(anIndex -> regla.siguiente(
                        celdas[(anIndex - 1 + n) % n],
                        celdas[anIndex],
                        celdas[(anIndex + 1) % n]))
                .toArray();
    }
}
