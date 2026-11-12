package turing.negocio.bloques;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import turing.modelo.Alfabeto;
import turing.modelo.CintaInfinita;
import turing.modelo.MaquinaTuring;
import turing.negocio.diagrama.CompiladorDiagrama;
import turing.negocio.diagrama.DiagramaBloques;
import turing.negocio.motor.MotorTuring;
import turing.negocio.motor.MotorTuringSecuencial;
import turing.negocio.motor.ResultadoEjecucion;

/**
 * Pruebas de cada bloque de construcción ejecutado como máquina independiente.
 * Las cintas esperadas muestran desde la posición -2 hasta dos celdas después de la entrada
 * y marcan con [ ] la celda bajo la cabeza.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
class BloquesTuringTest {

    private static final Alfabeto SIGMA = new Alfabeto("abc01#Δ", 'Δ');

    /**
     * Ejecuta un bloque solo y devuelve la cinta con la cabeza marcada.
     */
    private String ejecutar(String notacion, String cinta, int posicion) {
        DiagramaBloques diagrama = new DiagramaBloques(notacion)
                .nodo("B", notacion, "bloque bajo prueba")
                .arco("B", DiagramaBloques.CUALQUIERA, DiagramaBloques.ALTO);
        MaquinaTuring maquina = new CompiladorDiagrama().compilar(diagrama, SIGMA);
        MotorTuring motor = new MotorTuringSecuencial();
        CintaInfinita aTape = new CintaInfinita(cinta, 'Δ', posicion);
        motor.cargar(maquina, aTape);
        assertEquals(ResultadoEjecucion.DETENIDA_EN_ESTADO_FINAL, motor.ejecutar(10_000));
        return conCabeza(aTape, -2, cinta.length() + 1);
    }

    private String conCabeza(CintaInfinita aTape, int desde, int hasta) {
        StringBuilder result = new StringBuilder();
        for (long aPosition = desde; aPosition <= hasta; aPosition++) {
            char aSymbol = aTape.simboloEn(aPosition);
            result.append(aPosition == aTape.getPosicion() ? "[" + aSymbol + "]" : String.valueOf(aSymbol));
        }
        return result.toString();
    }

    @Test
    void rMueveUnLugarALaDerecha() {
        assertEquals("ΔΔab[c]ΔΔ", ejecutar("R", "abc", 1));
    }

    @Test
    void lMueveUnLugarALaIzquierda() {
        assertEquals("ΔΔ[a]bcΔΔ", ejecutar("L", "abc", 1));
    }

    @Test
    void xEscribeSinMoverse() {
        assertEquals("ΔΔa[#]cΔΔ", ejecutar("#", "abc", 1));
    }

    @Test
    void rxBuscaALaDerechaMoviendoseAlMenosUnaVez() {
        assertEquals("ΔΔa0b0[#]ΔΔ", ejecutar("R#", "a0b0#", 0));
        assertEquals("ΔΔa0b[0]#ΔΔ", ejecutar("R0", "a0b0#", 1));
    }

    @Test
    void rxConConjuntoSeDetieneEnCualquieraDeLosSimbolos() {
        assertEquals("ΔΔaa[b]c#ΔΔ", ejecutar("R{c,b}", "aabc#", 0));
    }

    @Test
    void lxBuscaALaIzquierda() {
        assertEquals("ΔΔ#ab[#]cΔΔ", ejecutar("L#", "#ab#c", 4));
        assertEquals("Δ[Δ]abcΔΔ", ejecutar("LΔ", "abc", 2));
    }

    @Test
    void srCorreLaCadenaDeLaIzquierdaUnLugarALaDerecha() {
        assertEquals("ΔΔ[Δ]abcΔΔ", ejecutar("SR", "abcΔ", 2));
    }

    @Test
    void srSobreBlancoNoHaceNada() {
        assertEquals("ΔΔabc[Δ]ΔΔ", ejecutar("SR", "abcΔ", 3));
    }

    @Test
    void slCorreLaCadenaDeLaDerechaUnLugarALaIzquierda() {
        assertEquals("ΔΔabc[Δ]ΔΔ", ejecutar("SL", "Δabc", 1));
    }

    @Test
    void slSoloMueveHastaElPrimerBlanco() {
        assertEquals("ΔΔb[Δ]Δc#ΔΔ", ejecutar("SL", "ΔbΔc#", 1));
    }

    @Test
    void fabricaRechazaNotacionesInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> FabricaBloques.crear("X{a"));
        assertThrows(IllegalArgumentException.class, () -> FabricaBloques.crear(""));
        assertThrows(IllegalArgumentException.class, () -> FabricaBloques.crear("Qa"));
    }

    @Test
    void notacionDeLosBloques() {
        assertEquals("R{0,1,#}", FabricaBloques.crear("R{0,1,#}").getNotacion());
        assertEquals("L#", FabricaBloques.crear("L#").getNotacion());
        assertEquals("SR", FabricaBloques.crear("SR").getNotacion());
    }
}
