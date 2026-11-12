package turing.servicio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import turing.controlador.CoordinadorApp;
import turing.modelo.CasoPrueba;
import turing.negocio.regla.ReglaLocal;

/**
 * Ejecuta todos los casos de casos_prueba.txt con la máquina de la Regla 110.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
class CasosPruebaArchivoTest {

    @Test
    void todosLosCasosDelArchivoSonCorrectos() {
        CoordinadorApp coordinador = new CoordinadorApp();
        coordinador.inicializarServicios();
        ReglaLocal regla = coordinador.crearRegla(110);
        List<CasoPrueba> someCases = coordinador.getCasosPrueba();
        assertFalse(someCases.isEmpty());
        for (CasoPrueba aCase : someCases) {
            ResultadoCaso aResult = coordinador.ejecutarCaso(aCase, regla);
            assertTrue(aResult.correcto(), "Caso de la línea " + aCase.numero() + ": " + aResult);
        }
    }
}
