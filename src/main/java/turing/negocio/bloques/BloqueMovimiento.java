package turing.negocio.bloques;

import java.util.ArrayList;
import java.util.List;

import turing.modelo.Alfabeto;
import turing.modelo.Estado;
import turing.modelo.Movimiento;
import turing.modelo.Transicion;

/**
 * Bloques R y L: mueven la cabeza un lugar sin importar el símbolo leído.
 * <pre>
 *   δ(q0, σ) = (qf, σ, D|I)   para todo σ ∈ Σ
 * </pre>
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public abstract class BloqueMovimiento implements BloqueTuring {

    private final Movimiento direccion;

    /**
     * @param direccion DERECHA para R, IZQUIERDA para L
     */
    protected BloqueMovimiento(Movimiento direccion) {
        this.direccion = direccion;
    }

    @Override
    public FragmentoMaquina compilar(GeneradorEstados generador, Alfabeto alfabeto, String etiqueta) {
        Estado inicio = generador.nuevo(etiqueta);
        Estado fin = generador.nuevo(etiqueta);
        List<Transicion> result = new ArrayList<>();
        for (char aSymbol : alfabeto.getSimbolos()) {
            result.add(new Transicion(inicio, aSymbol, fin, aSymbol, direccion));
        }
        return new FragmentoMaquina(inicio, fin, result);
    }
}
