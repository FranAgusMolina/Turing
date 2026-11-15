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
```

## Formato de la cinta

```
entrada:  K#celdas#            ej. 100#000000010000000#   (K = 4 en binario, N = 15)
salida:   0…0#G0#G1#…#GK#      el contador queda en 0 y la cinta conserva todas las generaciones
```

`casos_prueba.txt` tiene un caso por línea: `entrada ; generación final esperada ; descripción`.

## Interfaz

Una única pantalla de simulación: cinta con la cabeza y el estado, controles (paso, reproducir,
siguiente generación, hasta el final), diagrama espacio-tiempo que se va formando y tabla δ con la
transición aplicada.

Las marcas temporales que deja la máquina se dibujan en la cinta como una **X** cuyo color indica el
tipo de marca: una para el `0` ya procesado (símbolo `o` en δ) y otra para el `1` ya procesado
(símbolo `i` en δ).

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
│   └── regla/     ReglaLocal + Regla110 (tabla de verdad de la regla)
├── servicio/      MaquinaService, CasoPruebaService (+ Impl)
└── ui/            Interfaz, JavaFXLauncher (static bridge), InterfazJavaFXImpl, PresentadorSimulacion y componentes
```

El informe técnico está en [`docs/INFORME.md`](docs/INFORME.md).
