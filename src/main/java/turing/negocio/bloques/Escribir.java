package turing.negocio.bloques;

import java.util.ArrayList;
import java.util.List;

import turing.modelo.Alfabeto;
import turing.modelo.Estado;
import turing.modelo.Movimiento;
import turing.modelo.Transicion;

/**
 * Bloque x: escribe el símbolo x en la celda actual sin importar el símbolo leído.
 * Existe una máquina de este tipo por cada símbolo del alfabeto.
 * <pre>
 *   δ(q0, σ) = (qf, x, N)   para todo σ ∈ Σ
 * </pre>
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class Escribir implements BloqueTuring {

    private final char simbolo;

    /**
     * @param simbolo símbolo x que se escribe
     */
    public Escribir(char simbolo) {
        this.simbolo = simbolo;
    }

    @Override
    public String getNotacion() {
        return String.valueOf(simbolo);
    }

    @Override
    public String getDescripcion() {
        return "Escribe " + simbolo + " en la cinta sin importar el símbolo leído.";
    }

    @Override
    public FragmentoMaquina compilar(GeneradorEstados generador, Alfabeto alfabeto, String etiqueta) {
        if (!alfabeto.contiene(simbolo)) {
            throw new IllegalArgumentException("El símbolo " + simbolo + " no pertenece a Σ = " + alfabeto);
        }
        Estado inicio = generador.nuevo(etiqueta);
        Estado fin = generador.nuevo(etiqueta);
        List<Transicion> result = new ArrayList<>();
        for (char aSymbol : alfabeto.getSimbolos()) {
            result.add(new Transicion(inicio, aSymbol, fin, simbolo, Movimiento.NINGUNO));
        }
        return new FragmentoMaquina(inicio, fin, result);
    }
}
