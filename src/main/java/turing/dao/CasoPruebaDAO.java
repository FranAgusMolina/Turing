package turing.dao;

import java.util.List;

import turing.modelo.CasoPrueba;

/**
 * Acceso a los casos de prueba del trabajo práctico.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public interface CasoPruebaDAO {

    /**
     * @return todos los casos de prueba, en el orden del origen de datos
     */
    List<CasoPrueba> buscarTodos();
}
