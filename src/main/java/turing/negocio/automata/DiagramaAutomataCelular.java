package turing.negocio.automata;

import turing.negocio.diagrama.DiagramaBloques;
import turing.negocio.regla.ReglaLocal;

/**
 * Diseño de la máquina de Turing que simula un autómata celular elemental con
 * fronteras cíclicas, armada exclusivamente con bloques de construcción
 * (R, L, x, Rx, Lx).
 *
 * <h2>Formato de la cinta</h2>
 * <pre>
 *   entrada:  Δ K # G0 # Δ            (K en binario, cabeza sobre el primer dígito de K)
 *   salida:   Δ 0…0 # G0 # G1 # … # Gk # Δ
 * </pre>
 * Cada generación nueva se escribe a la derecha de la anterior, así la cinta
 * conserva toda la evolución.
 *
 * <h2>Símbolos auxiliares</h2>
 * <ul>
 *   <li>o = celda 0 ya procesada</li>
 *   <li>i = celda 1 ya procesada</li>
 * </ul>
 *
 * <h2>Algoritmo</h2>
 * <ol>
 *   <li><b>Contador</b>: se resta 1 a K en binario (borrow de derecha a izquierda).
 *       Si el borrow llega al blanco, K era 0: se restauran los dígitos a 0 y la máquina
 *       se detiene sobre el último #.</li>
 *   <li><b>Ubicar la generación actual</b> Gt: es el último bloque cerrado por #.</li>
 *   <li><b>Para cada celda</b> de Gt de izquierda a derecha:
 *     <ol type="a">
 *       <li>se busca la primera celda sin marcar y se la marca (c = centro);</li>
 *       <li>izquierda: la celda anterior (ya marcada) o, si es la primera, la última
 *           celda de Gt (frontera cíclica);</li>
 *       <li>se vuelve al centro (la celda marcada más a la derecha);</li>
 *       <li>derecha: la celda siguiente o, si es la última, la primera de Gt (cíclica);</li>
 *       <li>con (l, c, r) codificado en el camino recorrido del diagrama se escribe
 *           regla(l, c, r) al final de la cinta.</li>
 *     </ol></li>
 *   <li><b>Cerrar la generación</b>: se desmarca Gt, se escribe # al final de Gt+1 y se
 *       vuelve al contador.</li>
 * </ol>
 * La regla se inyecta (OCP): el mismo diseño sirve para cualquier regla elemental,
 * sólo cambia a qué rama de escritura (0 o 1) lleva cada uno de los 8 vecindarios.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public final class DiagramaAutomataCelular {

    private static final String ALTO = DiagramaBloques.ALTO;
    private static final String TODO = DiagramaBloques.CUALQUIERA;

    private DiagramaAutomataCelular() {
        throw new AssertionError("No instanciable");
    }

    /**
     * @param regla regla local del autómata
     * @return el diagrama de bloques de la máquina
     */
    public static DiagramaBloques crear(ReglaLocal regla) {
        DiagramaBloques result = new DiagramaBloques("MT autómata celular – " + regla.getNombre());
        agregarContador(result);
        agregarUbicacionGeneracion(result);
        for (int aCenter = 0; aCenter <= 1; aCenter++) {
            agregarVecinoIzquierdo(result, aCenter);
            for (int aLeft = 0; aLeft <= 1; aLeft++) {
                agregarVecinoDerecho(result, regla, aLeft, aCenter);
            }
        }
        agregarEscritura(result, 0);
        agregarEscritura(result, 1);
        agregarCierreGeneracion(result);
        return result;
    }

    /**
     * Fase 1: decrementa K en binario o se detiene si K = 0.
     */
    private static void agregarContador(DiagramaBloques diagrama) {
        diagrama
            .nodo("INICIO", "R#", "Desde el contador K avanza hasta el primer #")
            .nodo("DEC", "L", "Se para sobre el dígito menos significativo pendiente de K")
            .nodo("DEC_1", "1", "Borrow: 0 → 1 y sigue a la izquierda")
            .nodo("DEC_0", "0", "1 → 0: el decremento terminó, hay que calcular otra generación")
            .nodo("CERO", "R", "K era 0: recorre los dígitos para restaurarlos")
            .nodo("CERO_W", "0", "Restaura un dígito a 0")
            .nodo("FIN", "RΔ", "Va al final de la cinta")
            .nodo("FIN_L", "L", "Queda sobre el # que cierra la última generación")
            .arco("INICIO", TODO, "DEC")
            .arco("DEC", "0", "DEC_1")
            .arco("DEC", "1", "DEC_0")
            .arco("DEC", "Δ", "CERO")
            .arco("DEC_1", TODO, "DEC")
            .arco("DEC_0", TODO, "GEN_FIN")
            .arco("CERO", "1", "CERO_W")
            .arco("CERO", "#", "FIN")
            .arco("CERO_W", TODO, "CERO")
            .arco("FIN", TODO, "FIN_L")
            .arco("FIN_L", TODO, ALTO);
    }

    /**
     * Fase 2: ubica la generación actual y la próxima celda sin procesar.
     */
    private static void agregarUbicacionGeneracion(DiagramaBloques diagrama) {
        diagrama
            .nodo("GEN_FIN", "RΔ", "Va al final de la cinta")
            .nodo("GEN_CIERRE", "L", "Queda sobre el # que cierra Gt")
            .nodo("APERTURA", "L#", "Va al # que abre Gt")
            .nodo("SIG_CELDA", "R{0,1,#}", "Busca la primera celda sin marcar de Gt (o el # de cierre)")
            .nodo("VOLVER", "L#", "Tras escribir una celda de Gt+1 vuelve al # que cierra Gt")
            .arco("GEN_FIN", TODO, "GEN_CIERRE")
            .arco("GEN_CIERRE", TODO, "APERTURA")
            .arco("APERTURA", TODO, "SIG_CELDA")
            .arco("SIG_CELDA", "0", "MARCAR_C0")
            .arco("SIG_CELDA", "1", "MARCAR_C1")
            .arco("SIG_CELDA", "#", "DESMARCAR")
            .arco("VOLVER", TODO, "APERTURA");
    }

    /**
     * Fase 3a/3b: marca el centro c y obtiene el vecino izquierdo l.
     */
    private static void agregarVecinoIzquierdo(DiagramaBloques diagrama, int centro) {
        String sufijo = "_C" + centro;
        diagrama
            .nodo("MARCAR" + sufijo, String.valueOf(marca(centro)), "Marca la celda central c=" + centro)
            .nodo("IZQ" + sufijo, "L", "Mira la celda de la izquierda (c=" + centro + ")")
            .nodo("CICLO_IZQ" + sufijo, "R#", "Frontera cíclica: va al # de cierre de Gt (c=" + centro + ")")
            .nodo("CICLO_IZQ_L" + sufijo, "L", "Lee la última celda de Gt como vecino izquierdo (c=" + centro + ")")
            .arco("MARCAR" + sufijo, TODO, "IZQ" + sufijo)
            .arco("IZQ" + sufijo, "o", idRamaCentro(0, centro))
            .arco("IZQ" + sufijo, "i", idRamaCentro(1, centro))
            .arco("IZQ" + sufijo, "#", "CICLO_IZQ" + sufijo)
            .arco("CICLO_IZQ" + sufijo, TODO, "CICLO_IZQ_L" + sufijo)
            .arco("CICLO_IZQ_L" + sufijo, "0o", idRamaCentro(0, centro))
            .arco("CICLO_IZQ_L" + sufijo, "1i", idRamaCentro(1, centro));
    }

    /**
     * Fase 3c/3d/3e: vuelve al centro, obtiene el vecino derecho r y elige la rama
     * de escritura según regla(l, c, r).
     */
    private static void agregarVecinoDerecho(DiagramaBloques diagrama, ReglaLocal regla,
                                             int izquierda, int centro) {
        String sufijo = "_L" + izquierda + "C" + centro;
        String escribirSiR0 = idEscritura(regla.siguiente(izquierda, centro, 0));
        String escribirSiR1 = idEscritura(regla.siguiente(izquierda, centro, 1));
        diagrama
            .nodo(idRamaCentro(izquierda, centro), "R#",
                    "Conoce l=" + izquierda + ", c=" + centro + ": va al # de cierre de Gt")
            .nodo("CENTRO" + sufijo, "L{o,i}", "Vuelve al centro: la celda marcada más a la derecha")
            .nodo("DER" + sufijo, "R", "Mira la celda de la derecha")
            .nodo("CICLO_DER" + sufijo, "L#", "Frontera cíclica: va al # de apertura de Gt")
            .nodo("CICLO_DER_R" + sufijo, "R", "Lee la primera celda de Gt como vecino derecho")
            .arco(idRamaCentro(izquierda, centro), TODO, "CENTRO" + sufijo)
            .arco("CENTRO" + sufijo, TODO, "DER" + sufijo)
            .arco("DER" + sufijo, "0", escribirSiR0)
            .arco("DER" + sufijo, "1", escribirSiR1)
            .arco("DER" + sufijo, "#", "CICLO_DER" + sufijo)
            .arco("CICLO_DER" + sufijo, TODO, "CICLO_DER_R" + sufijo)
            .arco("CICLO_DER_R" + sufijo, "o", escribirSiR0)
            .arco("CICLO_DER_R" + sufijo, "i", escribirSiR1);
    }

    /**
     * Rama de escritura: agrega el valor v al final de Gt+1 y sigue hacia VOLVER.
     */
    private static void agregarEscritura(DiagramaBloques diagrama, int valor) {
        String id = idEscritura(valor);
        diagrama
            .nodo(id, "RΔ", "Va al final de la cinta para agregar la nueva celda")
            .nodo("ESCRIBIR_" + valor, String.valueOf(valor), "Escribe regla(l,c,r) = " + valor)
            .arco(id, TODO, "ESCRIBIR_" + valor)
            .arco("ESCRIBIR_" + valor, TODO, "VOLVER");
    }

    /**
     * Fase 4: desmarca Gt, cierra Gt+1 con # y vuelve al contador.
     */
    private static void agregarCierreGeneracion(DiagramaBloques diagrama) {
        diagrama
            .nodo("DESMARCAR", "L", "Recorre Gt hacia la izquierda quitando las marcas")
            .nodo("DESMARCAR_0", "0", "o → 0")
            .nodo("DESMARCAR_1", "1", "i → 1")
            .nodo("CERRAR_FIN", "RΔ", "Va al final de Gt+1")
            .nodo("CERRAR", "#", "Cierra Gt+1 con #")
            .nodo("VOLVER_K", "LΔ", "Vuelve al blanco que precede al contador K")
            .arco("DESMARCAR", "o", "DESMARCAR_0")
            .arco("DESMARCAR", "i", "DESMARCAR_1")
            .arco("DESMARCAR", "#", "CERRAR_FIN")
            .arco("DESMARCAR_0", TODO, "DESMARCAR")
            .arco("DESMARCAR_1", TODO, "DESMARCAR")
            .arco("CERRAR_FIN", TODO, "CERRAR")
            .arco("CERRAR", TODO, "VOLVER_K")
            .arco("VOLVER_K", TODO, "INICIO");
    }

    private static char marca(int valor) {
        return valor == 0 ? 'o' : 'i';
    }

    private static String idRamaCentro(int izquierda, int centro) {
        return "IR_CIERRE_L" + izquierda + "C" + centro;
    }

    private static String idEscritura(int valor) {
        return "ESC_FIN_" + valor;
    }
}
