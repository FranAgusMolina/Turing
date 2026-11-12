package turing.negocio.bloques;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import turing.modelo.Alfabeto;
import turing.modelo.Estado;
import turing.modelo.Movimiento;
import turing.modelo.Transicion;

/**
 * Bloques Rx y Lx: se desplazan hacia un lado hasta encontrar la primera x.
 * <p>
 * Se generaliza a un conjunto de símbolos X (por ejemplo R{0,1,#}) porque así la
 * máquina principal se describe con menos bloques; con un solo símbolo coincide
 * exactamente con el bloque de la cátedra. Siempre se mueve al menos una vez.
 * <pre>
 *   δ(q0, σ) = (q1, σ, M)            para todo σ ∈ Σ        (primer movimiento)
 *   δ(q1, σ) = (q1, σ, M)            si σ ∉ X               (sigue buscando)
 *   δ(q1, σ) = (qf, σ, N)            si σ ∈ X               (encontró)
 * </pre>
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public abstract class BloqueBusqueda implements BloqueTuring {

    private final Movimiento direccion;
    private final Set<Character> buscados;

    /**
     * @param direccion sentido de la búsqueda
     * @param buscados  conjunto X de símbolos que detienen la búsqueda (no vacío)
     */
    protected BloqueBusqueda(Movimiento direccion, Set<Character> buscados) {
        if (buscados.isEmpty()) {
            throw new IllegalArgumentException("El conjunto de símbolos buscados no puede ser vacío");
        }
        this.direccion = direccion;
        this.buscados = Collections.unmodifiableSet(new LinkedHashSet<>(buscados));
    }

    /**
     * @return la letra del bloque ("R" o "L")
     */
    protected abstract String getLetra();

    /**
     * @return el conjunto X de símbolos buscados
     */
    public Set<Character> getBuscados() {
        return buscados;
    }

    @Override
    public String getNotacion() {
        return getLetra() + notacionConjunto();
    }

    private String notacionConjunto() {
        if (buscados.size() == 1) {
            return String.valueOf(buscados.iterator().next());
        }
        StringBuilder result = new StringBuilder("{");
        for (char aSymbol : buscados) {
            if (result.length() > 1) {
                result.append(',');
            }
            result.append(aSymbol);
        }
        return result.append('}').toString();
    }

    @Override
    public FragmentoMaquina compilar(GeneradorEstados generador, Alfabeto alfabeto, String etiqueta) {
        Estado inicio = generador.nuevo(etiqueta);
        Estado buscando = generador.nuevo(etiqueta);
        Estado fin = generador.nuevo(etiqueta);
        List<Transicion> result = new ArrayList<>();
        for (char aSymbol : alfabeto.getSimbolos()) {
            result.add(new Transicion(inicio, aSymbol, buscando, aSymbol, direccion));
        }
        for (char aSymbol : alfabeto.getSimbolos()) {
            if (buscados.contains(aSymbol)) {
                result.add(new Transicion(buscando, aSymbol, fin, aSymbol, Movimiento.NINGUNO));
            } else {
                result.add(new Transicion(buscando, aSymbol, buscando, aSymbol, direccion));
            }
        }
        return new FragmentoMaquina(inicio, fin, result);
    }
}
