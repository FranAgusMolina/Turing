package turing.negocio.bloques;

import turing.modelo.Alfabeto;

/**
 * Contrato de un bloque de construcción de la "máquina de Turing simplificada"
 * (R, L, x, Rx, Lx, SR, SL).
 * <p>
 * Cada bloque es en sí mismo una pequeña máquina de Turing: sabe compilarse a un
 * fragmento de tabla δ sobre cualquier alfabeto. Esto permite combinar bloques en
 * un diagrama (patrón Composite) y obtener al final una única tabla de transiciones.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public interface BloqueTuring {

    /**
     * @return la notación del bloque tal como se usa en los diagramas (ej. "R", "L#", "SR")
     */
    String getNotacion();

    /**
     * @return descripción en lenguaje natural de lo que hace el bloque
     */
    String getDescripcion();

    /**
     * Traduce el bloque a transiciones de una máquina de Turing estándar.
     *
     * @param generador generador de estados compartido por todo el diagrama
     * @param alfabeto  alfabeto de trabajo Σ
     * @param etiqueta  etiqueta con la que se documentan los estados generados
     * @return el fragmento de máquina equivalente al bloque
     */
    FragmentoMaquina compilar(GeneradorEstados generador, Alfabeto alfabeto, String etiqueta);
}
