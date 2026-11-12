package turing.negocio.bloques;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Fábrica de bloques a partir de su notación textual (Factory Method).
 * Es el único lugar donde se hace "new" de bloques concretos: el resto del sistema
 * trabaja contra {@link BloqueTuring}.
 * <pre>
 *   "R"  "L"            mover un lugar
 *   "R#" "L{o,i}"       buscar a derecha / izquierda un símbolo o un conjunto
 *   "SR" "SL"           correr la cadena
 *   "1"  "Δ"            escribir un símbolo
 * </pre>
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public final class FabricaBloques {

    /** Notaciones de los bloques que ofrece la cátedra, en el orden de la diapositiva. */
    public static final List<String> CATALOGO = List.of("L", "R", "x", "Rx", "Lx", "SR", "SL");

    private FabricaBloques() {
        throw new AssertionError("No instanciable");
    }

    /**
     * Crea un bloque a partir de su notación.
     *
     * @param notacion notación del bloque
     * @return el bloque correspondiente
     * @throws IllegalArgumentException si la notación no es válida
     */
    public static BloqueTuring crear(String notacion) {
        if (notacion == null || notacion.isEmpty()) {
            throw new IllegalArgumentException("Notación de bloque vacía");
        }
        return switch (notacion) {
            case "R" -> new MoverDerecha();
            case "L" -> new MoverIzquierda();
            case "SR" -> new DesplazarDerecha();
            case "SL" -> new DesplazarIzquierda();
            default -> crearParametrizado(notacion);
        };
    }

    private static BloqueTuring crearParametrizado(String notacion) {
        if (notacion.length() == 1) {
            return new Escribir(notacion.charAt(0));
        }
        char letra = notacion.charAt(0);
        Set<Character> simbolos = parsearConjunto(notacion.substring(1));
        return switch (letra) {
            case 'R' -> new BuscarDerecha(simbolos);
            case 'L' -> new BuscarIzquierda(simbolos);
            default -> throw new IllegalArgumentException("Notación de bloque desconocida: " + notacion);
        };
    }

    /**
     * Interpreta "x" o "{x,y,z}".
     */
    private static Set<Character> parsearConjunto(String texto) {
        Set<Character> result = new LinkedHashSet<>();
        if (texto.length() == 1) {
            result.add(texto.charAt(0));
            return result;
        }
        if (!texto.startsWith("{") || !texto.endsWith("}")) {
            throw new IllegalArgumentException("Conjunto de símbolos mal formado: " + texto);
        }
        for (String aToken : texto.substring(1, texto.length() - 1).split(",")) {
            String limpio = aToken.trim();
            if (limpio.length() != 1) {
                throw new IllegalArgumentException("Símbolo inválido en el conjunto: '" + aToken + "'");
            }
            result.add(limpio.charAt(0));
        }
        return result;
    }
}
