# Turing

Máquina de Turing que simula la evolución de un **autómata celular elemental (Regla 110)** con fronteras cíclicas.
Trabajo Práctico Final – Fundamentos Teóricos de la Informática.

La máquina se diseña con los **bloques de construcción** de la cátedra (`R`, `L`, `x`, `Rx`, `Lx`, `SR`, `SL`),
se compila a una única tabla δ (127 estados, 705 transiciones) y se ejecuta paso a paso. La interfaz JavaFX muestra
la cinta, la tabla δ y el **patrón triangular** de la regla, que se lee directamente de la cinta.

## Requisitos

- JDK 21 o superior. En Fedora 44 el JDK disponible es el 25: `sudo dnf install java-25-openjdk-devel`
- Maven 3.9+ (`sudo dnf install maven`)

JavaFX, JUnit y Log4j los descarga Maven.

## Cómo se ejecuta

```bash
mvn javafx:run            # interfaz gráfica
mvn test                  # pruebas (bloques, máquina vs. simulación directa, casos del .txt)
mvn compile exec:java     # genera docs/diagrama_bloques.md, docs/tabla_transiciones.md y docs/resultados_casos.md
mvn compile exec:java -Dexec.args="30"   # lo mismo para otra regla
```

## Formato de la cinta

```
entrada:  K#celdas#            ej. 100#000000010000000#   (K = 4 en binario, N = 15)
salida:   0…0#G0#G1#…#GK#      el contador queda en 0 y la cinta conserva todas las generaciones
```

`casos_prueba.txt` tiene un caso por línea: `entrada ; generación final esperada ; descripción`.

## Pestañas de la interfaz

| Pestaña | Qué muestra |
|---|---|
| Simulación | Cinta con la cabeza y el estado, controles (paso, reproducir, siguiente generación, hasta el final), diagrama espacio-tiempo que se va formando y tabla δ con la transición aplicada |
| Patrón triangular | Ejecuta la MT con N y K grandes en segundo plano, dibuja el triángulo y lo verifica contra la simulación directa |
| Máquina (diagrama y δ) | El diagrama de bloques del diseño y la tabla δ compilada |
| Bloques de construcción | Laboratorio para ejecutar cada bloque (L, R, x, Rx, Lx, SR, SL) por separado y ver su δ |
| Casos de prueba | Ejecuta todos los casos de `casos_prueba.txt` |

El selector de regla del encabezado permite cambiar a otras reglas elementales (101, 30, 90, …): el diseño de la
máquina es el mismo, sólo cambia a qué rama de escritura lleva cada vecindario.

## Estructura (MVC)

```
src/main/java/turing/
├── aplicacion/    AplicacionPrincipal (main puro), Constantes, ExportadorTabla (consola)
├── conexion/      Factory (instancia el DAO configurado en factory.properties)
├── controlador/   CoordinadorApp: ensambla servicios y vista
├── dao/           CasoPruebaDAO + secuencial/CasoPruebaDAOSecuencial (lee casos_prueba.txt)
├── modelo/        Cinta, CintaInfinita, Estado, Transicion, Movimiento, Alfabeto, MaquinaTuring, CasoPrueba
├── negocio/
│   ├── bloques/   BloqueTuring y los bloques R, L, x, Rx, Lx, SR, SL + FabricaBloques
│   ├── diagrama/  DiagramaBloques (composición de bloques) y CompiladorDiagrama (→ tabla δ)
│   ├── automata/  DiagramaAutomataCelular (el diseño de la MT), codificación de la cinta, simuladores
│   ├── motor/     MotorTuring + MotorTuringSecuencial
│   └── regla/     ReglaLocal + ReglaElemental (número de Wolfram)
├── servicio/      MaquinaService, CasoPruebaService (+ Impl)
└── ui/            Interfaz, JavaFXLauncher (static bridge), InterfazJavaFXImpl, presentadores y componentes
```

El informe técnico está en [`docs/INFORME.md`](docs/INFORME.md).
