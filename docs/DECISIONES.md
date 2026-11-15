# Decisiones de diseño

Resumen conceptual de las decisiones tomadas en el trabajo: qué se eligió, por qué, y qué se paga a cambio.
El detalle formal está en [`INFORME.md`](INFORME.md), el diagrama en [`diagrama_bloques.md`](diagrama_bloques.md)
y la tabla δ en [`tabla_transiciones.md`](tabla_transiciones.md).

---

## 1. Idea general

**La máquina se diseña con bloques y se compila a una única tabla δ.**
No se escribió la tabla de transiciones a mano. El algoritmo se expresa como un diagrama de 51 nodos hechos con
las máquinas elementales de la cátedra, y un compilador lo traduce a una MT estándar (S, Σ, δ, s0, F) de
127 estados y 705 transiciones.

- *Por qué:* permite razonar en un nivel alto (buscar, marcar, escribir) sin perder la definición formal, y
  la tabla sale correcta por construcción en vez de por revisión manual.
- *Costo:* la máquina resultante no es mínima. Cada flecha del diagrama se vuelve una transición sin
  movimiento, así que hay estados y pasos "de enlace" que una tabla hecha a mano evitaría.

**Lo que se ejecuta es la MT compilada, no el diagrama.**
El motor sólo conoce δ. Los bloques existen en tiempo de diseño; en la tabla queda apenas una etiqueta que
indica de qué bloque proviene cada estado, para poder leerla.

---

## 2. Representación en la cinta

| Decisión | Motivo | Consecuencia |
|---|---|---|
| Formato `K#G0#` con la cabeza sobre el primer dígito de K | Es el formato del enunciado y la convención de la cátedra | La entrada se valida *antes* de llegar a la máquina; la MT asume una cinta bien formada |
| K en **binario**, a la izquierda | Lo pide el enunciado | El control de iteraciones es una resta binaria con préstamo, no un simple borrado de palotes |
| Cada generación se escribe **a continuación** de la anterior | La cinta queda como registro completo de la evolución, y el patrón triangular se puede leer directamente de ella | La cinta crece K·N celdas, y volver al contador cuesta cada vez más (ver §5) |
| `#` como único delimitador | Un solo símbolo separa el contador y todas las generaciones | "La generación actual" se define por posición: es el último tramo cerrado por `#` |
| Al terminar, K queda en `0…0` y la cabeza sobre el último `#` | Estado final reconocible y determinista | El contador se consume: K no se conserva en la salida |

### Alfabeto: Σ = {0, 1, #, o, i, Δ}

Sólo se agregan **dos símbolos auxiliares**, `o` e `i`, que significan "celda 0 ya procesada" y "celda 1 ya
procesada".

- **Marcar en lugar de borrar.** La celda procesada sigue haciendo falta: es el vecino izquierdo de la
  siguiente y, si es la primera, el vecino derecho cíclico de la última. La marca conserva el valor y a la
  vez registra el avance.
- **La marca es el único estado guardado en la cinta.** No hay marcadores de posición ni copias de trabajo;
  "dónde voy" se deduce de la frontera entre celdas marcadas y sin marcar.
- Las marcas son temporales: al cerrar cada generación se restauran (`o→0`, `i→1`), de modo que la cinta
  final sólo contiene 0, 1 y `#`.

---

## 3. El algoritmo

### 3.1 Control de iteraciones

Se **decrementa K** en cada vuelta en lugar de contar hacia arriba y comparar.

- Restar 1 en binario es local: de derecha a izquierda, cada `0` pasa a `1` hasta encontrar un `1`, que pasa
  a `0`.
- La condición de parada sale gratis: si el préstamo llega al blanco, K era 0. Los dígitos quedaron en `1`,
  se restauran a `0` y la máquina se detiene.
- Con K = 0 no se calcula nada y la cinta queda igual a la entrada.

### 3.2 Dónde vive la memoria: cinta vs. estado

Es la decisión central del diseño. La máquina necesita recordar tres bits (l, c, r) mientras viaja hasta el
final de la cinta para escribir el resultado.

**Se guardan en el estado, no en la cinta.** El diagrama se bifurca: 2 ramas según c, 4 según (l, c) y, al
leer r, los 8 vecindarios confluyen en sólo 2 ramas de escritura (`ESC_FIN_0` y `ESC_FIN_1`).

- *Por qué:* evita símbolos extra y viajes adicionales para dejar y recoger anotaciones.
- *Consecuencia elegante:* **la tabla de verdad de la regla es exactamente la elección de rama al leer r.**
  La Regla 110 no se "calcula"; está cableada en la topología del diagrama.
- *Costo:* el tramo "vecino derecho" está cuadruplicado (una copia por cada par (l, c)), lo que explica buena
  parte de los 127 estados.

Reparto final: **la cinta recuerda el avance** (marcas), **el estado recuerda el vecindario** (ramas).

### 3.3 Orden de procesamiento

Las celdas se procesan de izquierda a derecha, una por vez, y cada resultado se **agrega al final de la
cinta**. Como el orden de lectura coincide con el de escritura, la nueva generación se forma sola en el orden
correcto y no hace falta ningún puntero de escritura: "el final" siempre es el primer blanco a la derecha.

Para volver al centro después de mirar el vecino izquierdo se usa un invariante en lugar de un marcador: **el
centro es la celda marcada más a la derecha**.

### 3.4 Fronteras cíclicas

Se resuelven con **desvíos explícitos**, sin duplicar celdas en la cinta.

- Si al mirar a la izquierda se lee `#`, la celda era la primera: se va al `#` de cierre y se lee la última.
- Si al mirar a la derecha se lee `#`, la celda era la última: se va al `#` de apertura y se lee la primera.

El `#` cumple entonces dos funciones: delimita y **detecta el borde**. La alternativa de copiar las celdas de
los extremos como "fantasmas" habría ensuciado el formato de salida y obligado a mantener las copias.

Un detalle que el diseño tiene en cuenta: el vecino cíclico izquierdo todavía está sin marcar (se lee como
`0`/`1` o como `o`/`i` si N = 1), mientras que el vecino cíclico derecho ya está marcado (se lee como
`o`/`i`). Por eso cada desvío acepta símbolos distintos.

### 3.5 Detención

- **Parada normal:** un único estado final `qH`.
- **Parada anómala:** δ es parcial; si no hay transición para el par actual, la máquina se detiene sin llegar
  a `qH`. Se decidió no "completar" δ con transiciones de relleno justamente para que un error de diseño se
  manifieste en lugar de quedar oculto. No ocurre en ninguna prueba.

---

## 4. Bloques y compilación

**Cada bloque es una máquina con una entrada y una salida.** Eso los hace componibles: el compilador no
necesita saber qué hace un bloque, sólo dónde empieza y dónde termina.

**Una flecha es una transición sin movimiento:** `δ(salida_A, σ) = (entrada_B, σ, N)`. La decisión de rama se
toma mirando el símbolo que quedó bajo la cabeza, tal como se leen los diagramas de la cátedra.

**Búsquedas generalizadas a conjuntos.** `Rx` y `Lx` aceptan un conjunto de símbolos (`R{0,1,#}`, `L{o,i}`).
Es lo que permite expresar "primera celda sin marcar o fin de generación" en un solo bloque. Siguen la
semántica de la cátedra: primero se mueven y después buscan.

**Determinismo por orden de declaración.** Si dos flechas aceptan el mismo símbolo, gana la primera
declarada. Simplifica el uso del comodín `*` sin introducir no determinismo.

**`SR` y `SL` se implementaron pero no se usan.** Están porque forman parte del juego de bloques de la
cátedra y tienen sus pruebas, pero el algoritmo elegido nunca necesita correr cadenas: agregar al final de la
cinta evita todos los desplazamientos.

**La regla se inyecta.** El diseño de la MT está separado de la tabla de verdad: la regla sólo decide a qué
rama de escritura va cada vecindario. Hoy la única implementación es la Regla 110.

---

## 5. Costo asumido

La máquina prioriza **claridad sobre eficiencia**.

- Por cada celda, la cabeza recorre la generación actual y la parte ya escrita de la nueva varias veces.
- Una generación cuesta O(N²) pasos; volver al contador cuesta O(t·N) porque la cinta conserva el historial.
- Total: **O(K·N² + K²·N)**. Ejemplo: N = 101, K = 100 son unos 5,5 millones de pasos.

El término K²·N es el precio directo de conservar todas las generaciones. Se aceptó porque el registro
completo es lo que hace visible el patrón, y porque el contraste con la simulación directa (un bucle) es
parte de lo que el trabajo quiere mostrar.

---

## 6. Límites y validación

| Límite | Valor | Motivo |
|---|---|---|
| Longitud mínima de la hilera | N ≥ 8 | Requisito del enunciado |
| Tamaño de K | hasta 20 bits | Cota práctica: el costo crece con K² (ver §5) |
| Pasos por ejecución | 200.000.000 | Protección ante bucles; al superarlo la ejecución se aborta |

**La validación ocurre fuera de la máquina.** La MT no reconoce el lenguaje de entradas válidas: el software
rechaza las cintas mal formadas antes de ejecutar. Es una restricción de la aplicación, no de la máquina (el
diagrama en sí funciona con N menores).

---

## 7. Verificación

**Oráculo independiente.** La Regla 110 se implementó dos veces por caminos que no comparten código: la MT y
un simulador directo con un bucle común. Ambos se comparan en 100 entradas aleatorias. Un error tendría que
repetirse igual en los dos para pasar inadvertido.

**Pruebas por niveles**, de lo pequeño a lo completo:

1. Cada bloque ejecutado como máquina independiente.
2. La máquina completa contra el oráculo.
3. Casos dirigidos a lo delicado: K = 0, ceros a la izquierda en K, y vecindarios que **sólo existen si los
   extremos son adyacentes** (111 en la celda 0, 101 en la celda N−1).
4. Los casos de `casos_prueba.txt`, incluidas entradas que deben ser rechazadas.

Los valores esperados del archivo se calcularon aparte, no con el programa que se está probando.

---

## 8. Arquitectura del software

- **Describir, construir y ejecutar son cosas distintas.** `MaquinaTuring` sólo describe la quíntupla; el
  compilador la construye; el motor la ejecuta. Ninguno conoce los detalles del otro.
- **Capas (MVC):** modelo, negocio, servicio, controlador, ui y dao. El coordinador ensambla las dependencias
  y es el único punto donde se conectan vista y servicios.
- **La cinta infinita es un arreglo que crece a demanda** hacia ambos lados, duplicando su tamaño. Modela la
  cinta infinita de la definición sin reservar memoria de más.
- **El origen de los casos de prueba es configurable** (`factory.properties`): hoy un archivo de texto con un
  caso por línea, reemplazable sin tocar el resto.
- **Los documentos se generan, no se escriben.** El diagrama, la tabla δ y los resultados de `docs/` salen del
  mismo diagrama que se ejecuta, así que no pueden quedar desactualizados respecto de la máquina.

### Interfaz

- **El patrón se lee de la cinta.** El diagrama espacio-tiempo no viene de una simulación paralela: se
  interpreta el contenido real de la cinta, incluso a medio calcular. Lo que se ve es lo que la MT hizo.
- **Las marcas se dibujan como una X de color** en vez de mostrar `o`/`i`, para que se distinga a simple vista
  qué celdas ya se procesaron sin confundirlas con datos.
- **Granularidades de ejecución:** un paso, reproducción continua, siguiente generación y hasta el final. Un
  paso sirve para seguir δ; las otras, para ver el patrón sin esperar millones de pasos.
- **La ejecución larga se reparte por cuadros** con un presupuesto de pasos, para que la interfaz no se
  congele.

---

## 9. Síntesis

| Tensión | Se eligió | Se resignó |
|---|---|---|
| Tabla a mano vs. bloques compilados | Bloques compilados | Minimalidad de la máquina |
| Recordar en la cinta vs. en el estado | Avance en la cinta, vecindario en el estado | Menos estados |
| Sobrescribir vs. conservar generaciones | Conservar todas | Tiempo (término K²·N) y espacio |
| Borrar vs. marcar celdas | Marcar | Dos símbolos más en Σ |
| Celdas fantasma vs. desvíos en los bordes | Desvíos | Recorridos extra en los extremos |
| δ total vs. δ parcial | Parcial, con parada anómala | — |
| Validar en la MT vs. en el software | En el software | La MT no es reconocedora del formato |
