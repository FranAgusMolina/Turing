package turing.negocio.diagrama;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Etiqueta de una flecha del diagrama de bloques: el conjunto de símbolos que,
 * leídos al terminar el bloque origen, hacen que se siga por esa flecha.
 * Una condición vacía significa "cualquier símbolo" (flecha sin etiqueta).
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public final class Condicion {

    private static final Condicion CUALQUIERA = new Condicion(Set.of());

    private final Set<Character> simbolos;

    private Condicion(Set<Character> simbolos) {
        this.simbolos = Collections.unmodifiableSet(new LinkedHashSet<>(simbolos));
    }

    /**
     * @return la condición que acepta cualquier símbolo
     */
    public static Condicion cualquiera() {
        return CUALQUIERA;
    }

    /**
     * @param simbolos cada carácter de la cadena es un símbolo aceptado
     * @return la condición correspondiente
     */
    public static Condicion simbolos(String simbolos) {
        Set<Character> result = new LinkedHashSet<>();
        for (char aSymbol : simbolos.toCharArray()) {
            result.add(aSymbol);
        }
        return new Condicion(result);
    }

    /**
     * @param simbolo símbolo leído
     * @return true si la flecha se puede tomar con ese símbolo
     */
    public boolean acepta(char simbolo) {
        return simbolos.isEmpty() || simbolos.contains(simbolo);
    }

    @Override
    public String toString() {
        if (simbolos.isEmpty()) {
            return "*";
        }
        StringBuilder result = new StringBuilder();
        for (char aSymbol : simbolos) {
            if (!result.isEmpty()) {
                result.append(',');
            }
            result.append(aSymbol);
        }
        return result.toString();
    }
}
