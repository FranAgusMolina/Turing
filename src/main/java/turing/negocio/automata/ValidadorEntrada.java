package turing.negocio.automata;

import java.util.Optional;
import java.util.regex.Pattern;

import turing.aplicacion.Constantes;

/**
 * Valida el formato de la cinta de entrada que pide el enunciado:
 * K en binario, #, hilera binaria de longitud N ≥ 8, #.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public final class ValidadorEntrada {

    private static final Pattern FORMATO = Pattern.compile("[01]+#[01]+#");
    private static final int MAXIMO_BITS_K = 20;

    private ValidadorEntrada() {
        throw new AssertionError("No instanciable");
    }

    /**
     * @param cinta texto ingresado
     * @return un mensaje de error o vacío si la entrada es válida
     */
    public static Optional<String> validar(String cinta) {
        if (cinta == null || cinta.isBlank()) {
            return Optional.of("La cinta está vacía.");
        }
        if (!FORMATO.matcher(cinta).matches()) {
            return Optional.of("Formato inválido. Se espera K#celdas# con K binario y celdas 0/1, "
                    + "por ejemplo 100#000000010000000#");
        }
        String[] partes = cinta.split("#");
        if (partes[0].length() > MAXIMO_BITS_K) {
            return Optional.of("K tiene demasiados dígitos (máximo " + MAXIMO_BITS_K + " bits).");
        }
        if (partes[1].length() < Constantes.LONGITUD_MINIMA) {
            return Optional.of("La hilera debe tener longitud N ≥ " + Constantes.LONGITUD_MINIMA
                    + " (tiene " + partes[1].length() + ").");
        }
        return Optional.empty();
    }
}
