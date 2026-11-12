package turing.servicio;

import java.util.List;

import turing.modelo.CasoPrueba;
import turing.negocio.regla.ReglaLocal;

/**
 * Servicio de casos de prueba: los lee y los ejecuta con la máquina de Turing.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public interface CasoPruebaService {

    /**
     * @return todos los casos del archivo
     */
    List<CasoPrueba> buscarTodos();

    /**
     * Ejecuta un caso con la máquina de la regla indicada y lo compara contra la
     * simulación directa y contra el valor esperado del archivo.
     *
     * @param caso  caso a ejecutar
     * @param regla regla del autómata
     * @return el resultado de la ejecución
     */
    ResultadoCaso ejecutar(CasoPrueba caso, ReglaLocal regla);
}
