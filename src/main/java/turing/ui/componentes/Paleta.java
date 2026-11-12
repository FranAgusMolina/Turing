package turing.ui.componentes;

import javafx.scene.paint.Color;

/**
 * Colores que se dibujan sobre Canvas (el resto del estilo está en estilos.css).
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public final class Paleta {

    public static final Color FONDO = Color.web("#0e1318");
    public static final Color SUPERFICIE = Color.web("#151c24");
    public static final Color BORDE = Color.web("#2a3542");
    public static final Color TEXTO = Color.web("#dbe3ec");
    public static final Color TEXTO_TENUE = Color.web("#6f7d8c");

    /** Cabeza lecto-escritora y elementos activos. */
    public static final Color ACENTO = Color.web("#f5b942");

    /** Celda viva del autómata. */
    public static final Color VIVA = Color.web("#e9eef4");

    /** Celda muerta del autómata. */
    public static final Color MUERTA = Color.web("#18212b");

    /** Celdas marcadas (o, i) por la máquina. */
    public static final Color MARCADA = Color.web("#3cc2b0");

    /** Separadores #. */
    public static final Color SEPARADOR = Color.web("#a48cf0");

    private Paleta() {
        throw new AssertionError("No instanciable");
    }
}
