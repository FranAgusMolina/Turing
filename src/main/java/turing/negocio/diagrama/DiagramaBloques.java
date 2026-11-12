package turing.negocio.diagrama;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import turing.negocio.bloques.BloqueTuring;
import turing.negocio.bloques.FabricaBloques;

/**
 * Diagrama de una máquina de Turing simplificada: nodos que son bloques de
 * construcción (R, L, x, Rx, Lx, SR, SL) unidos por flechas etiquetadas con el
 * símbolo leído al terminar cada bloque.
 * <p>
 * Se arma con una interfaz fluida:
 * <pre>
 *   new DiagramaBloques("ejemplo")
 *       .nodo("A", "R#", "busca el separador")
 *       .nodo("B", "L",  "retrocede")
 *       .arco("A", "*", "B")
 *       .arco("B", "*", DiagramaBloques.ALTO);
 * </pre>
 * El primer nodo agregado es el inicial. Si al terminar un bloque ninguna flecha
 * acepta el símbolo leído, la máquina se detiene sin transición (parada anómala).
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class DiagramaBloques {

    /** Id reservado del nodo de parada. */
    public static final String ALTO = "ALTO";

    /** Etiqueta de flecha que acepta cualquier símbolo. */
    public static final String CUALQUIERA = "*";

    private final String nombre;
    private final Map<String, NodoDiagrama> nodos = new LinkedHashMap<>();
    private final List<Arco> arcos = new ArrayList<>();

    /**
     * @param nombre nombre de la máquina
     */
    public DiagramaBloques(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Agrega un nodo creando el bloque a partir de su notación.
     *
     * @param id         id único del nodo
     * @param notacion   notación del bloque (ver {@link FabricaBloques})
     * @param comentario para qué sirve el nodo
     * @return este diagrama
     */
    public DiagramaBloques nodo(String id, String notacion, String comentario) {
        return nodo(id, FabricaBloques.crear(notacion), comentario);
    }

    /**
     * Agrega un nodo con un bloque ya construido.
     *
     * @param id         id único del nodo
     * @param bloque     bloque del nodo
     * @param comentario para qué sirve el nodo
     * @return este diagrama
     */
    public DiagramaBloques nodo(String id, BloqueTuring bloque, String comentario) {
        if (ALTO.equals(id) || nodos.containsKey(id)) {
            throw new IllegalArgumentException("Id de nodo repetido o reservado: " + id);
        }
        nodos.put(id, new NodoDiagrama(id, bloque, comentario));
        return this;
    }

    /**
     * Agrega una flecha.
     *
     * @param origen   id del nodo origen
     * @param simbolos símbolos de la etiqueta ("*" = cualquiera)
     * @param destino  id del nodo destino o {@link #ALTO}
     * @return este diagrama
     */
    public DiagramaBloques arco(String origen, String simbolos, String destino) {
        Condicion condicion = CUALQUIERA.equals(simbolos)
                ? Condicion.cualquiera()
                : Condicion.simbolos(simbolos);
        arcos.add(new Arco(origen, condicion, destino));
        return this;
    }

    /**
     * Verifica que todas las flechas unan nodos existentes.
     *
     * @throws IllegalStateException si hay flechas colgadas o el diagrama está vacío
     */
    public void validar() {
        if (nodos.isEmpty()) {
            throw new IllegalStateException("El diagrama no tiene nodos");
        }
        for (Arco anArc : arcos) {
            if (!nodos.containsKey(anArc.origen())) {
                throw new IllegalStateException("Flecha desde un nodo inexistente: " + anArc);
            }
            if (!ALTO.equals(anArc.destino()) && !nodos.containsKey(anArc.destino())) {
                throw new IllegalStateException("Flecha hacia un nodo inexistente: " + anArc);
            }
        }
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * @return el nodo inicial (el primero agregado)
     */
    public NodoDiagrama getInicial() {
        return nodos.values().iterator().next();
    }

    /**
     * @return los nodos en orden de inserción
     */
    public List<NodoDiagrama> getNodos() {
        return List.copyOf(nodos.values());
    }

    /**
     * @param id id del nodo
     * @return el nodo o null
     */
    public NodoDiagrama getNodo(String id) {
        return nodos.get(id);
    }

    /**
     * @return todas las flechas en orden de inserción
     */
    public List<Arco> getArcos() {
        return Collections.unmodifiableList(arcos);
    }

    /**
     * @param origen id del nodo
     * @return las flechas que salen de ese nodo, en orden de prioridad
     */
    public List<Arco> arcosDesde(String origen) {
        return arcos.stream().filter(anArc -> anArc.origen().equals(origen)).toList();
    }
}
