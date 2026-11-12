package turing.modelo;

/**
 * Estado de la unidad de control finita.
 *
 * @param nombre identificador único del estado (por ejemplo "q17")
 * @param bloque etiqueta del bloque de construcción del que proviene
 *               (por ejemplo "A2 · R{0,1,#}"); sólo se usa para documentar la tabla δ
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public record Estado(String nombre, String bloque) {

    @Override
    public boolean equals(Object otro) {
        return otro instanceof Estado estado && estado.nombre.equals(nombre);
    }

    @Override
    public int hashCode() {
        return nombre.hashCode();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
