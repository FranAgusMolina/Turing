package turing.modelo;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Alfabeto de trabajo Σ de una máquina de Turing.
 * Conserva el orden de inserción para que la tabla δ se imprima siempre igual.
 * Por convención de la cátedra Σ ⊃ {Δ}.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public final class Alfabeto {

    private final Set<Character> simbolos;
    private final char blanco;

    /**
     * @param simbolos cadena con todos los símbolos (debe incluir el blanco)
     * @param blanco   símbolo blanco de la cinta
     * @throws IllegalArgumentException si el blanco no pertenece al alfabeto
     */
    public Alfabeto(String simbolos, char blanco) {
        Set<Character> result = new LinkedHashSet<>();
        for (char aSymbol : simbolos.toCharArray()) {
            result.add(aSymbol);
        }
        if (!result.contains(blanco)) {
            throw new IllegalArgumentException("El alfabeto debe contener al blanco " + blanco);
        }
        this.simbolos = Collections.unmodifiableSet(result);
        this.blanco = blanco;
    }

    /**
     * @return los símbolos en orden, inmodificables
     */
    public Set<Character> getSimbolos() {
        return simbolos;
    }

    /**
     * @return el símbolo blanco
     */
    public char getBlanco() {
        return blanco;
    }

    /**
     * @param simbolo símbolo a consultar
     * @return true si el símbolo pertenece a Σ
     */
    public boolean contiene(char simbolo) {
        return simbolos.contains(simbolo);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        for (char aSymbol : simbolos) {
            if (result.length() > 1) {
                result.append(", ");
            }
            result.append(aSymbol);
        }
        return result.append('}').toString();
    }
}
