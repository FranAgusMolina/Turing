package turing.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Máquina de Turing formal T = (S, Σ, δ, s0, F).
 * Es un objeto inmutable: sólo describe la máquina. Quien la ejecuta es
 * {@link turing.negocio.motor.MotorTuring}.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public final class MaquinaTuring {

    private final String nombre;
    private final Alfabeto alfabeto;
    private final Estado estadoInicial;
    private final Set<Estado> estadosFinales;
    private final Set<Estado> estados;
    private final List<Transicion> transiciones;
    private final Map<Estado, Map<Character, Transicion>> delta;

    /**
     * @param nombre         nombre descriptivo de la máquina
     * @param alfabeto       alfabeto de trabajo Σ
     * @param estadoInicial  s0
     * @param estadosFinales F (estados de parada)
     * @param transiciones   δ como lista de transiciones
     * @throws IllegalArgumentException si δ no es una función (dos transiciones para el mismo par)
     *                                  o usa símbolos fuera de Σ
     */
    public MaquinaTuring(String nombre, Alfabeto alfabeto, Estado estadoInicial,
                         Set<Estado> estadosFinales, List<Transicion> transiciones) {
        this.nombre = nombre;
        this.alfabeto = alfabeto;
        this.estadoInicial = estadoInicial;
        this.estadosFinales = Collections.unmodifiableSet(new LinkedHashSet<>(estadosFinales));
        this.transiciones = List.copyOf(transiciones);
        this.delta = indexar(transiciones);
        this.estados = Collections.unmodifiableSet(recolectarEstados());
    }

    private Map<Estado, Map<Character, Transicion>> indexar(List<Transicion> someTransitions) {
        Map<Estado, Map<Character, Transicion>> result = new HashMap<>();
        for (Transicion aTransition : someTransitions) {
            validarSimbolo(aTransition.simboloLeido());
            validarSimbolo(aTransition.simboloEscrito());
            Transicion previa = result
                    .computeIfAbsent(aTransition.origen(), estado -> new HashMap<>())
                    .put(aTransition.simboloLeido(), aTransition);
            if (previa != null) {
                throw new IllegalArgumentException("δ no es función: dos transiciones para "
                        + aTransition.origen() + " con " + aTransition.simboloLeido());
            }
        }
        return result;
    }

    private void validarSimbolo(char simbolo) {
        if (!alfabeto.contiene(simbolo)) {
            throw new IllegalArgumentException("Símbolo fuera de Σ: " + simbolo);
        }
    }

    private Set<Estado> recolectarEstados() {
        Set<Estado> result = new LinkedHashSet<>();
        result.add(estadoInicial);
        for (Transicion aTransition : transiciones) {
            result.add(aTransition.origen());
            result.add(aTransition.destino());
        }
        result.addAll(estadosFinales);
        return result;
    }

    /**
     * Consulta δ(estado, símbolo). Como δ es una función parcial puede no estar definida.
     *
     * @param estado  estado actual
     * @param simbolo símbolo leído
     * @return la transición, o null si δ no está definida para ese par
     */
    public Transicion transicion(Estado estado, char simbolo) {
        Map<Character, Transicion> porSimbolo = delta.get(estado);
        return porSimbolo == null ? null : porSimbolo.get(simbolo);
    }

    /**
     * @param estado estado a consultar
     * @return true si pertenece a F
     */
    public boolean esFinal(Estado estado) {
        return estadosFinales.contains(estado);
    }

    public String getNombre() {
        return nombre;
    }

    public Alfabeto getAlfabeto() {
        return alfabeto;
    }

    public Estado getEstadoInicial() {
        return estadoInicial;
    }

    public Set<Estado> getEstadosFinales() {
        return estadosFinales;
    }

    public Set<Estado> getEstados() {
        return estados;
    }

    /**
     * @return δ completa en el orden en que fue generada
     */
    public List<Transicion> getTransiciones() {
        return transiciones;
    }

    /**
     * @param estado estado origen
     * @return las transiciones que salen de ese estado
     */
    public List<Transicion> transicionesDesde(Estado estado) {
        Map<Character, Transicion> porSimbolo = delta.get(estado);
        return porSimbolo == null ? List.of() : new ArrayList<>(porSimbolo.values());
    }
}
