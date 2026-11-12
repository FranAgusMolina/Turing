package turing.negocio.bloques;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import turing.modelo.Alfabeto;
import turing.modelo.Estado;
import turing.modelo.Movimiento;
import turing.modelo.Transicion;

/**
 * Bloques SR y SL: corren un lugar una cadena delimitada por el primer blanco.
 * <p>
 * Sea M la dirección del corrimiento y M' la opuesta. La cadena es la que va desde la
 * posición actual (inclusive) hacia M' hasta el primer blanco (exclusive). Cada símbolo
 * se copia una celda hacia M, la celda que queda libre en el extremo se pone en Δ y la
 * cabeza termina sobre esa celda libre.
 * <p>
 * La máquina recuerda el símbolo que está transportando en el estado (un estado
 * "llevar[σ]" por cada símbolo no blanco):
 * <pre>
 *   δ(q0, Δ)        = (qf, Δ, N)               cadena vacía: no hace nada
 *   δ(q0, σ)        = (llevar[σ], σ, M)        toma σ y avanza hacia M
 *   δ(llevar[σ], τ) = (volver, σ, M')          escribe σ una celda más allá
 *   δ(volver, τ)    = (leer, τ, M')            vuelve a la celda siguiente de la cadena
 *   δ(leer, Δ)      = (limpiar, Δ, M)          terminó la cadena
 *   δ(leer, σ)      = (llevar[σ], σ, M)        toma el siguiente símbolo
 *   δ(limpiar, τ)   = (qf, Δ, N)               deja libre la primera celda
 * </pre>
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public abstract class BloqueDesplazamiento implements BloqueTuring {

    private final Movimiento direccion;

    /**
     * @param direccion sentido hacia el que se corre la cadena
     */
    protected BloqueDesplazamiento(Movimiento direccion) {
        this.direccion = direccion;
    }

    @Override
    public FragmentoMaquina compilar(GeneradorEstados generador, Alfabeto alfabeto, String etiqueta) {
        char blanco = alfabeto.getBlanco();
        Movimiento regreso = direccion.opuesto();

        Estado inicio = generador.nuevo(etiqueta);
        Estado volver = generador.nuevo(etiqueta);
        Estado leer = generador.nuevo(etiqueta);
        Estado limpiar = generador.nuevo(etiqueta);
        Map<Character, Estado> llevar = new LinkedHashMap<>();
        for (char aSymbol : alfabeto.getSimbolos()) {
            if (aSymbol != blanco) {
                llevar.put(aSymbol, generador.nuevo(etiqueta));
            }
        }
        Estado fin = generador.nuevo(etiqueta);

        List<Transicion> result = new ArrayList<>();
        result.add(new Transicion(inicio, blanco, fin, blanco, Movimiento.NINGUNO));
        result.add(new Transicion(leer, blanco, limpiar, blanco, direccion));
        llevar.forEach((aSymbol, aCarrier) -> {
            result.add(new Transicion(inicio, aSymbol, aCarrier, aSymbol, direccion));
            result.add(new Transicion(leer, aSymbol, aCarrier, aSymbol, direccion));
        });
        for (char aSymbol : alfabeto.getSimbolos()) {
            llevar.forEach((aCarried, aCarrier) ->
                    result.add(new Transicion(aCarrier, aSymbol, volver, aCarried, regreso)));
            result.add(new Transicion(volver, aSymbol, leer, aSymbol, regreso));
            result.add(new Transicion(limpiar, aSymbol, fin, blanco, Movimiento.NINGUNO));
        }
        return new FragmentoMaquina(inicio, fin, result);
    }
}
