package turing.negocio.automata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Historia de generaciones del autómata (diagrama espacio-tiempo).
 * La fila 0 es la configuración inicial. Mientras la máquina de Turing está
 * trabajando puede existir además una generación parcial (sólo algunas celdas).
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public final class EvolucionAutomata {

    private final List<int[]> generaciones;
    private final int[] parcial;

    /**
     * @param generaciones generaciones completas, en orden
     * @param parcial      celdas ya calculadas de la generación en curso (vacío si no hay)
     */
    public EvolucionAutomata(List<int[]> generaciones, int[] parcial) {
        List<int[]> result = new ArrayList<>();
        generaciones.forEach(aGeneration -> result.add(aGeneration.clone()));
        this.generaciones = List.copyOf(result);
        this.parcial = parcial.clone();
    }

    /**
     * @param generaciones generaciones completas
     */
    public EvolucionAutomata(List<int[]> generaciones) {
        this(generaciones, new int[0]);
    }

    /**
     * @return cantidad de generaciones completas (incluida la inicial)
     */
    public int cantidad() {
        return generaciones.size();
    }

    /**
     * @param indice número de generación
     * @return copia de la generación
     */
    public int[] generacion(int indice) {
        return generaciones.get(indice).clone();
    }

    /**
     * @return copia de la última generación completa
     */
    public int[] ultima() {
        return generacion(generaciones.size() - 1);
    }

    /**
     * @return celdas de la generación en curso
     */
    public int[] getParcial() {
        return parcial.clone();
    }

    /**
     * Compara sólo las generaciones completas.
     *
     * @param otra otra evolución
     * @return true si tienen las mismas generaciones completas
     */
    public boolean mismasGeneraciones(EvolucionAutomata otra) {
        if (otra.cantidad() != cantidad()) {
            return false;
        }
        for (int anIndex = 0; anIndex < cantidad(); anIndex++) {
            if (!Arrays.equals(generaciones.get(anIndex), otra.generaciones.get(anIndex))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return una línea por generación, con el formato "Gen t: 0 0 1 ..."
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int anIndex = 0; anIndex < generaciones.size(); anIndex++) {
            result.append("Gen ").append(anIndex).append(": ");
            for (int aCell : generaciones.get(anIndex)) {
                result.append(aCell).append(' ');
            }
            result.setLength(result.length() - 1);
            result.append('\n');
        }
        return result.toString();
    }
}
