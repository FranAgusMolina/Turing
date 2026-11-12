package turing.modelo;

/**
 * Caso de prueba leído del archivo casos_prueba.txt (una línea por caso).
 *
 * @param numero      número de línea del caso dentro del archivo
 * @param entrada     cinta de entrada, por ejemplo "100#000000010000000#"
 * @param esperada    generación final esperada (vacío si no se indicó)
 * @param descripcion para qué sirve el caso
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public record CasoPrueba(int numero, String entrada, String esperada, String descripcion) {

    /**
     * @return true si el caso indica qué generación final se espera
     */
    public boolean tieneEsperada() {
        return esperada != null && !esperada.isEmpty();
    }
}
