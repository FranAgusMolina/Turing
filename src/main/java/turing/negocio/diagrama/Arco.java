package turing.negocio.diagrama;

/**
 * Flecha del diagrama de bloques.
 *
 * @param origen    id del nodo de donde sale
 * @param condicion símbolos con los que se toma
 * @param destino   id del nodo al que llega (o {@link DiagramaBloques#ALTO})
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public record Arco(String origen, Condicion condicion, String destino) {

    @Override
    public String toString() {
        return origen + " --" + condicion + "--> " + destino;
    }
}
