package turing.aplicacion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import turing.controlador.CoordinadorApp;
import turing.modelo.CasoPrueba;
import turing.modelo.MaquinaTuring;
import turing.modelo.Transicion;
import turing.negocio.diagrama.Arco;
import turing.negocio.diagrama.DiagramaBloques;
import turing.negocio.diagrama.NodoDiagrama;
import turing.negocio.regla.ReglaLocal;
import turing.servicio.ResultadoCaso;

/**
 * Herramienta de consola que genera la documentación para el informe:
 * <ul>
 *   <li>docs/diagrama_bloques.md: el diagrama de bloques de la máquina</li>
 *   <li>docs/tabla_transiciones.md: la tabla δ completa</li>
 *   <li>docs/resultados_casos.md: la ejecución de todos los casos de prueba</li>
 * </ul>
 * Uso: {@code mvn compile exec:java [-Dexec.args="110"]}
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class ExportadorTabla {

    public static void main(String[] args) throws IOException {
        int numero = args.length > 0 ? Integer.parseInt(args[0]) : Constantes.REGLA_POR_DEFECTO;
        CoordinadorApp coordinador = new CoordinadorApp();
        coordinador.inicializarServicios();
        ReglaLocal regla = coordinador.crearRegla(numero);

        Path carpeta = Path.of("docs");
        Files.createDirectories(carpeta);
        escribir(carpeta.resolve("diagrama_bloques.md"), diagrama(coordinador.obtenerDiagrama(regla)));
        escribir(carpeta.resolve("tabla_transiciones.md"), tabla(coordinador.obtenerMaquina(regla)));
        escribir(carpeta.resolve("resultados_casos.md"), casos(coordinador, regla));
    }

    private static void escribir(Path archivo, String contenido) throws IOException {
        Files.writeString(archivo, contenido, StandardCharsets.UTF_8);
        System.out.println("Generado " + archivo.toAbsolutePath());
    }

    private static String diagrama(DiagramaBloques diagrama) {
        StringBuilder result = new StringBuilder("# Diagrama de bloques – ").append(diagrama.getNombre())
                .append("\n\nNodo inicial: `").append(diagrama.getInicial().id()).append("`\n\n")
                .append("| Nodo | Bloque | Flechas (símbolo → destino) | Función |\n|---|---|---|---|\n");
        for (NodoDiagrama aNode : diagrama.getNodos()) {
            String flechas = diagrama.arcosDesde(aNode.id()).stream()
                    .map(ExportadorTabla::flecha)
                    .collect(Collectors.joining("<br>"));
            result.append("| `").append(aNode.id()).append("` | `").append(aNode.bloque().getNotacion())
                    .append("` | ").append(flechas).append(" | ").append(aNode.comentario()).append(" |\n");
        }
        return result.toString();
    }

    private static String flecha(Arco anArc) {
        return "`" + anArc.condicion() + "` → `" + anArc.destino() + "`";
    }

    private static String tabla(MaquinaTuring maquina) {
        StringBuilder result = new StringBuilder("# Tabla de transiciones – ").append(maquina.getNombre())
                .append("\n\n- |S| = ").append(maquina.getEstados().size())
                .append("\n- Σ = ").append(maquina.getAlfabeto())
                .append("\n- s0 = ").append(maquina.getEstadoInicial())
                .append("\n- F = ").append(maquina.getEstadosFinales())
                .append("\n- |δ| = ").append(maquina.getTransiciones().size())
                .append("\n\n| Estado | Lee | Nuevo estado | Escribe | Mov | Bloque |\n|---|---|---|---|---|---|\n");
        for (Transicion aTransition : maquina.getTransiciones()) {
            result.append("| ").append(aTransition.origen())
                    .append(" | ").append(aTransition.simboloLeido())
                    .append(" | ").append(aTransition.destino())
                    .append(" | ").append(aTransition.simboloEscrito())
                    .append(" | ").append(aTransition.movimiento().getNotacion())
                    .append(" | ").append(aTransition.origen().bloque()).append(" |\n");
        }
        return result.toString();
    }

    private static String casos(CoordinadorApp coordinador, ReglaLocal regla) {
        List<CasoPrueba> someCases = coordinador.getCasosPrueba();
        StringBuilder result = new StringBuilder("# Resultados de los casos de prueba – ")
                .append(regla.getNombre())
                .append("\n\n| Línea | Entrada | Generación final (MT) | Pasos | Resultado | Descripción |\n")
                .append("|---|---|---|---|---|---|\n");
        for (CasoPrueba aCase : someCases) {
            ResultadoCaso aResult = coordinador.ejecutarCaso(aCase, regla);
            String veredicto = aResult.correcto()
                    ? (aResult.error().isEmpty() ? "✔ correcto" : "✔ rechazada: " + aResult.error())
                    : "✘ " + (aResult.error().isEmpty() ? "difiere" : aResult.error());
            result.append("| ").append(aCase.numero())
                    .append(" | `").append(aCase.entrada())
                    .append("` | `").append(aResult.obtenida())
                    .append("` | ").append(aResult.pasos())
                    .append(" | ").append(veredicto)
                    .append(" | ").append(aCase.descripcion()).append(" |\n");
        }
        return result.toString();
    }
}
