package turing.servicio;

import turing.modelo.MaquinaTuring;
import turing.negocio.diagrama.DiagramaBloques;
import turing.negocio.motor.MotorTuring;
import turing.negocio.regla.ReglaLocal;

/**
 * Servicio que construye y entrega las máquinas de Turing de la aplicación.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public interface MaquinaService {

    /**
     * @param regla regla del autómata
     * @return el diagrama de bloques de la máquina que simula esa regla
     */
    DiagramaBloques obtenerDiagrama(ReglaLocal regla);

    /**
     * @param regla regla del autómata
     * @return la máquina compilada (se cachea por número de regla)
     */
    MaquinaTuring obtenerMaquina(ReglaLocal regla);

    /**
     * Compila un único bloque de construcción como máquina independiente
     * (bloque → ALTO). Se usa en el laboratorio de bloques.
     *
     * @param notacion notación del bloque, por ejemplo "R#" o "SL"
     * @return la máquina equivalente al bloque
     */
    MaquinaTuring compilarBloque(String notacion);

    /**
     * @return un motor nuevo, listo para cargar una máquina
     */
    MotorTuring nuevoMotor();
}
