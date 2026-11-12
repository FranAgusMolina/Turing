package turing.conexion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory para la creación de instancias de DAOs y componentes configurables.
 * Utiliza el patrón Singleton con caché para garantizar una única instancia por tipo.
 * La implementación concreta de cada componente se define
 * en el archivo factory.properties.
 *
 * @author Francisco Agustin Molina
 * @version 1.0
 */
public final class Factory {
    /**
     * Caché thread-safe de instancias singleton indexadas por nombre lógico.
     * Utiliza ConcurrentHashMap para garantizar acceso concurrente sin bloqueos explícitos.
     */
    private static final ConcurrentHashMap<String, Object> INSTANCIAS = new ConcurrentHashMap<>();

    /**
     * Logger para registrar eventos de creación de instancias y errores.
     */
    private static final Logger LOG = LogManager.getLogger(Factory.class);


    /**
     * Constructor privado para prevenir instanciación.
     * Esta clase solo contiene métodos estáticos y no debe ser instanciada.
     *
     * @throws AssertionError siempre, para prevenir instanciación mediante reflexión
     */
    private Factory() {
        throw new AssertionError("No instanciable");
    }


    /**
     * Obtiene una instancia única y tipada del componente configurado en factory.properties.
     * Este método implementa el patrón Singleton con verificación de tipos en tiempo de ejecución.
     * Si la instancia no existe, la crea automáticamente y la almacena en caché.
     * Las instancias son thread-safe y reutilizables.
     *
     * @param <T>          el tipo esperado de la instancia
     * @param objName      nombre lógico del componente (ej: "CASO_PRUEBA")
     * @param expectedType la clase del tipo esperado, usada para validación type-safe
     * @return la instancia única del componente solicitado, casteada al tipo esperado
     * @throws ClassCastException       si la instancia obtenida no es del tipo esperado,
     *                                  indicando un error de configuración en factory.properties
     * @throws RuntimeException         si ocurre un error al crear la instancia
     * @throws IllegalArgumentException si la clave no existe en factory.properties
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstancia(String objName, Class<T> expectedType) {
        Object instance = INSTANCIAS.computeIfAbsent(objName, Factory::crearInstancia);

        // VERIFICACIÓN TYPE-SAFE
        if (!expectedType.isInstance(instance)) {
            String error = String.format(
                    "ERROR CONFIGURACIÓN: %s → Esperado: %s, Obtenido: %s",
                    objName, expectedType.getName(), instance.getClass().getName());
            LOG.error(error);
            throw new ClassCastException(error);
        }

        return (T) instance;
    }


    /**
     * Crea una nueva instancia del componente especificado por su clave.
     * Este método es invocado internamente por {@link #getInstancia(String, Class)} cuando
     * la instancia no existe en caché. Lee la configuración de factory.properties,
     * obtiene el nombre completo de la clase e instancia el objeto mediante reflexión.
     *
     * @param clave nombre lógico del componente a crear
     * @return la instancia recién creada del componente
     * @throws RuntimeException         si ocurre algún error durante la creación
     *                                  (reflexión, constructor inaccesible, etc.)
     * @throws IllegalArgumentException si la clave no existe en factory.properties
     */
    private static Object crearInstancia(String clave) {
        try {
            LOG.info("Creando instancia para: {}", clave);
            ResourceBundle rb = ResourceBundle.getBundle("factory");

            if (!rb.containsKey(clave)) {
                throw new IllegalArgumentException("Clave no encontrada en factory.properties: " + clave);
            }

            String className = rb.getString(clave);
            Object instance = Class.forName(className).getDeclaredConstructor().newInstance();

            LOG.debug("Instancia creada: {} → {}", clave, className);
            return instance;

        } catch (Exception ex) {
            LOG.error("Error creando instancia para: {}", clave, ex);
            throw new RuntimeException("Error Factory al crear: " + clave, ex);
        }
    }

    /**
     * Limpia completamente el caché de instancias singleton.
     * Este método elimina todas las instancias almacenadas, forzando la creación
     * de nuevas instancias en las próximas invocaciones de {@link #getInstancia(String, Class)}.
     * Es útil principalmente para pruebas unitarias o cuando se necesita reinicializar
     * el sistema con una configuración diferente.
     *
     * <p><strong>Advertencia:</strong> Este método debe usarse con precaución en entornos
     * de producción, ya que puede afectar el rendimiento y el estado de la aplicación.</p>
     */
    public static void clearCache() {
        INSTANCIAS.clear();
        LOG.info("Cache de Factory limpiado");
    }

}
