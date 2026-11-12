package turing.negocio.automata;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Pruebas del formato de cinta y de su validación.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
class CodificadorCintaTest {

    @Test
    void decodificaLosEjemplosDelEnunciado() {
        EntradaAutomata entrada = CodificadorCinta.decodificar("1110#01101001#");
        assertEquals(14, entrada.generaciones());
        assertArrayEquals(new int[]{0, 1, 1, 0, 1, 0, 0, 1}, entrada.celdas());
        assertEquals("1110#01101001#", CodificadorCinta.codificar(entrada));
    }

    @Test
    void rechazaEntradasMalFormadas() {
        assertTrue(ValidadorEntrada.validar("").isPresent());
        assertTrue(ValidadorEntrada.validar("100#0101#").isPresent(), "N < 8");
        assertTrue(ValidadorEntrada.validar("1#000000002#").isPresent());
        assertTrue(ValidadorEntrada.validar("#00000000#").isPresent(), "falta K");
        assertTrue(ValidadorEntrada.validar("1#00000000").isPresent(), "falta # final");
        assertThrows(IllegalArgumentException.class, () -> CodificadorCinta.decodificar("abc"));
    }

    @Test
    void leeUnaCintaAMedioProcesar() {
        EvolucionAutomata evolucion = CodificadorCinta.leerEvolucion("01#oii00010#01");
        assertEquals(1, evolucion.cantidad());
        assertArrayEquals(new int[]{0, 1, 1, 0, 0, 0, 1, 0}, evolucion.generacion(0));
        assertArrayEquals(new int[]{0, 1}, evolucion.getParcial());
    }
}
