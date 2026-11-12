package turing.negocio.automata;

import java.util.ArrayList;
import java.util.List;

import turing.aplicacion.Constantes;

/**
 * Traduce entre los datos del autómata y el contenido de la cinta.
 * <pre>
 *   EntradaAutomata(K=4, 000000010000000)  ⇄  "100#000000010000000#"
 * </pre>
 * También interpreta una cinta a medio procesar: las marcas o/i se leen como 0/1 y
 * lo que está después del último # es la generación parcial.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public final class CodificadorCinta {

    private CodificadorCinta() {
        throw new AssertionError("No instanciable");
    }

    /**
     * @param entrada datos del autómata
     * @return el contenido inicial de la cinta
     */
    public static String codificar(EntradaAutomata entrada) {
        return Integer.toBinaryString(entrada.generaciones())
                + Constantes.SEPARADOR + celdasATexto(entrada.celdas()) + Constantes.SEPARADOR;
    }

    /**
     * Interpreta una cinta de entrada ya validada.
     *
     * @param cinta contenido "K#celdas#"
     * @return los datos del autómata
     * @throws IllegalArgumentException si el formato no es válido
     */
    public static EntradaAutomata decodificar(String cinta) {
        ValidadorEntrada.validar(cinta).ifPresent(error -> {
            throw new IllegalArgumentException(error);
        });
        String[] partes = cinta.split(String.valueOf(Constantes.SEPARADOR));
        return new EntradaAutomata(Integer.parseInt(partes[0], 2), textoACeldas(partes[1]));
    }

    /**
     * Lee la evolución registrada en la cinta (completa o a medio calcular).
     *
     * @param contenido contenido no blanco de la cinta
     * @return generaciones completas y la generación parcial
     */
    public static EvolucionAutomata leerEvolucion(String contenido) {
        int primerSeparador = contenido.indexOf(Constantes.SEPARADOR);
        if (primerSeparador < 0) {
            return new EvolucionAutomata(List.of());
        }
        String resto = contenido.substring(primerSeparador + 1);
        int ultimoSeparador = resto.lastIndexOf(Constantes.SEPARADOR);
        List<int[]> result = new ArrayList<>();
        if (ultimoSeparador >= 0) {
            for (String aBlock : resto.substring(0, ultimoSeparador).split(String.valueOf(Constantes.SEPARADOR))) {
                result.add(textoACeldas(aBlock));
            }
        }
        int[] parcial = textoACeldas(resto.substring(ultimoSeparador + 1));
        return new EvolucionAutomata(result, parcial);
    }

    /**
     * @param celdas celdas 0/1
     * @return la hilera como texto, por ejemplo "00110"
     */
    public static String celdasATexto(int[] celdas) {
        StringBuilder result = new StringBuilder(celdas.length);
        for (int aCell : celdas) {
            result.append(aCell == 0 ? Constantes.CERO : Constantes.UNO);
        }
        return result.toString();
    }

    /**
     * Convierte texto a celdas. Las marcas o/i cuentan como 0/1; cualquier otro
     * símbolo (por ejemplo un blanco) se ignora.
     *
     * @param texto hilera de símbolos
     * @return celdas 0/1
     */
    public static int[] textoACeldas(String texto) {
        return texto.chars()
                .filter(aSymbol -> esCero((char) aSymbol) || esUno((char) aSymbol))
                .map(aSymbol -> esUno((char) aSymbol) ? 1 : 0)
                .toArray();
    }

    private static boolean esCero(char simbolo) {
        return simbolo == Constantes.CERO || simbolo == Constantes.CERO_MARCADO;
    }

    private static boolean esUno(char simbolo) {
        return simbolo == Constantes.UNO || simbolo == Constantes.UNO_MARCADO;
    }
}
