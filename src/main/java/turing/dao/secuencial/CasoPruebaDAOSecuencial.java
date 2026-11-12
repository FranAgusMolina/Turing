package turing.dao.secuencial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import turing.dao.CasoPruebaDAO;
import turing.modelo.CasoPrueba;

/**
 * Lee los casos de prueba de un archivo de texto con un caso por línea:
 * <pre>
 *   entrada ; generación final esperada ; descripción
 * </pre>
 * Las líneas vacías y las que empiezan con "//" se ignoran. El archivo se busca
 * primero en el directorio de trabajo y, si no existe, dentro del classpath.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public class CasoPruebaDAOSecuencial implements CasoPruebaDAO {

    private static final Logger LOG = LogManager.getLogger(CasoPruebaDAOSecuencial.class);
    private static final String COMENTARIO = "//";
    private static final String SEPARADOR_CAMPOS = ";";

    private final String nombreArchivo;

    /**
     * Toma el nombre del archivo de secuencial.properties (clave "casos_prueba").
     */
    public CasoPruebaDAOSecuencial() {
        this(ResourceBundle.getBundle("secuencial").getString("casos_prueba"));
    }

    /**
     * @param nombreArchivo ruta o nombre del archivo de casos
     */
    public CasoPruebaDAOSecuencial(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    @Override
    public List<CasoPrueba> buscarTodos() {
        try (BufferedReader lector = new BufferedReader(abrir())) {
            return parsear(lector.lines().toList());
        } catch (IOException e) {
            LOG.error("No se pudo leer {}", nombreArchivo, e);
            throw new IllegalStateException("No se pudo leer el archivo de casos: " + nombreArchivo, e);
        }
    }

    private Reader abrir() throws IOException {
        Path archivo = Path.of(nombreArchivo);
        if (Files.exists(archivo)) {
            LOG.info("Leyendo casos de prueba de {}", archivo.toAbsolutePath());
            return Files.newBufferedReader(archivo, StandardCharsets.UTF_8);
        }
        InputStream recurso = getClass().getClassLoader().getResourceAsStream(nombreArchivo);
        if (recurso == null) {
            throw new IOException("No existe el archivo ni el recurso " + nombreArchivo);
        }
        LOG.info("Leyendo casos de prueba del classpath: {}", nombreArchivo);
        return new InputStreamReader(recurso, StandardCharsets.UTF_8);
    }

    private List<CasoPrueba> parsear(List<String> lineas) {
        List<CasoPrueba> result = new ArrayList<>();
        for (int anIndex = 0; anIndex < lineas.size(); anIndex++) {
            String aLine = lineas.get(anIndex).strip();
            if (aLine.isEmpty() || aLine.startsWith(COMENTARIO)) {
                continue;
            }
            String[] campos = aLine.split(SEPARADOR_CAMPOS, 3);
            result.add(new CasoPrueba(anIndex + 1,
                    campos[0].strip(),
                    campos.length > 1 ? campos[1].strip() : "",
                    campos.length > 2 ? campos[2].strip() : ""));
        }
        LOG.debug("Se leyeron {} casos de prueba", result.size());
        return result;
    }
}
