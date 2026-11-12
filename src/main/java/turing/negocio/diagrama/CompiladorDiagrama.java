package turing.negocio.diagrama;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import turing.modelo.Alfabeto;
import turing.modelo.Estado;
import turing.modelo.MaquinaTuring;
import turing.modelo.Movimiento;
import turing.modelo.Transicion;
import turing.negocio.bloques.FragmentoMaquina;
import turing.negocio.bloques.GeneradorEstados;

/**
 * Traduce un {@link DiagramaBloques} a una {@link MaquinaTuring} estándar con una
 * única tabla δ.
 * <p>
 * Fase 1: cada nodo se compila a su fragmento (entrada, salida, transiciones).<br>
 * Fase 2: las flechas se convierten en transiciones sin movimiento desde la salida
 * del nodo origen hacia la entrada del nodo destino:
 * <pre>
 *   δ(salida_origen, σ) = (entrada_destino, σ, N)   para cada σ que acepta la flecha
 * </pre>
 * Si dos flechas aceptan el mismo símbolo gana la primera que se declaró.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class CompiladorDiagrama {

    /** Nombre del estado de parada (sp en la notación de la cátedra). */
    public static final String NOMBRE_ESTADO_ALTO = "qH";

    /**
     * @param diagrama diagrama a compilar
     * @param alfabeto alfabeto de trabajo Σ
     * @return la máquina de Turing equivalente
     */
    public MaquinaTuring compilar(DiagramaBloques diagrama, Alfabeto alfabeto) {
        diagrama.validar();
        GeneradorEstados generador = new GeneradorEstados();
        Estado alto = generador.conNombre(NOMBRE_ESTADO_ALTO, DiagramaBloques.ALTO);

        Map<String, FragmentoMaquina> fragmentos = compilarNodos(diagrama, alfabeto, generador);
        List<Transicion> result = new ArrayList<>();
        fragmentos.values().forEach(aFragment -> result.addAll(aFragment.transiciones()));
        result.addAll(enlazarArcos(diagrama, alfabeto, fragmentos, alto));

        Estado inicial = fragmentos.get(diagrama.getInicial().id()).entrada();
        return new MaquinaTuring(diagrama.getNombre(), alfabeto, inicial, Set.of(alto), result);
    }

    private Map<String, FragmentoMaquina> compilarNodos(DiagramaBloques diagrama, Alfabeto alfabeto,
                                                        GeneradorEstados generador) {
        Map<String, FragmentoMaquina> result = new LinkedHashMap<>();
        for (NodoDiagrama aNode : diagrama.getNodos()) {
            result.put(aNode.id(), aNode.bloque().compilar(generador, alfabeto, aNode.etiqueta()));
        }
        return result;
    }

    private List<Transicion> enlazarArcos(DiagramaBloques diagrama, Alfabeto alfabeto,
                                          Map<String, FragmentoMaquina> fragmentos, Estado alto) {
        List<Transicion> result = new ArrayList<>();
        for (NodoDiagrama aNode : diagrama.getNodos()) {
            Estado salida = fragmentos.get(aNode.id()).salida();
            List<Arco> someArcs = diagrama.arcosDesde(aNode.id());
            for (char aSymbol : alfabeto.getSimbolos()) {
                someArcs.stream()
                        .filter(anArc -> anArc.condicion().acepta(aSymbol))
                        .findFirst()
                        .map(anArc -> destinoDe(anArc, fragmentos, alto))
                        .ifPresent(aTarget -> result.add(
                                new Transicion(salida, aSymbol, aTarget, aSymbol, Movimiento.NINGUNO)));
            }
        }
        return result;
    }

    private Estado destinoDe(Arco anArc, Map<String, FragmentoMaquina> fragmentos, Estado alto) {
        return DiagramaBloques.ALTO.equals(anArc.destino())
                ? alto
                : fragmentos.get(anArc.destino()).entrada();
    }
}
