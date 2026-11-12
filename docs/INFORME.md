# Simulación de un Autómata Celular Unidimensional (Regla 110) con una Máquina de Turing

**Trabajo Práctico Final – Fundamentos Teóricos de la Informática**

**Integrantes:** _completar_

**Fecha de entrega:** 16 de noviembre

---

## 1. Introducción

El objetivo del trabajo es construir una Máquina de Turing (MT) que, a partir de una configuración inicial
binaria de longitud N y un número de generaciones K, calcule la evolución de un autómata celular elemental
según la **Regla 110**, con fronteras cíclicas, dejando registradas en la cinta todas las generaciones.

Además de la MT, se desarrolló una aplicación Java/JavaFX que permite ejecutar la máquina paso a paso,
inspeccionar su tabla de transiciones y visualizar el diagrama espacio-tiempo del autómata, donde aparece el
patrón triangular característico de la regla.

## 2. Marco teórico

### 2.1 Máquina de Turing

Siguiendo la definición de la cátedra, una MT es T = (S, Σ, δ, s0, F) donde S es un conjunto finito de
estados, Σ el alfabeto de trabajo (con Δ ∈ Σ, el blanco), δ : S × Σ → S × Σ × {I, D, N} una función parcial,
s0 el estado inicial y F el conjunto de estados de parada. La cinta es infinita en ambas direcciones y la
cabeza comienza sobre el primer carácter de la entrada.

### 2.2 Máquinas de Turing simplificadas (bloques de construcción)

Para diseñar máquinas complejas se usan máquinas elementales que se combinan en un diagrama: las flechas entre
bloques se etiquetan con el símbolo leído al terminar el bloque anterior.

| Bloque | Comportamiento |
|---|---|
| `R` | Se mueve un lugar a la derecha sin importar el símbolo leído. |
| `L` | Se mueve un lugar a la izquierda sin importar el símbolo leído. |
| `x` | Escribe x en la celda actual (una máquina por símbolo). |
| `Rx` | Se desplaza a la derecha hasta la primera x. |
| `Lx` | Se desplaza a la izquierda hasta la primera x. |
| `SR` | Corre un lugar a la derecha la cadena que está a la izquierda, desde la posición actual hasta el primer blanco. |
| `SL` | Corre un lugar a la izquierda la cadena que está a la derecha, desde la posición actual hasta el primer blanco. |

### 2.3 Autómatas celulares elementales y la Regla 110

Un autómata celular elemental es una hilera de celdas binarias que evoluciona en tiempo discreto: el nuevo
valor de cada celda depende sólo de ella y de sus dos vecinas. Hay 2⁸ = 256 reglas posibles; Wolfram las
numera leyendo la tabla de verdad como un número binario. La Regla 110 (0110 1110₂) es:

| Patrón (l c r) | 111 | 110 | 101 | 100 | 011 | 010 | 001 | 000 |
|---|---|---|---|---|---|---|---|---|
| Nuevo estado | 0 | 1 | 1 | 0 | 1 | 1 | 1 | 0 |

Matthew Cook demostró (2004) que la Regla 110 es **Turing-completa**: puede simular cualquier MT. En este
trabajo se recorre el camino inverso: una MT simula a la Regla 110.

## 3. Diseño de la máquina

### 3.1 Formato de la cinta

```
entrada:   Δ  K  #  G0  #  Δ                  ej.  100#000000010000000#
salida:    Δ  0…0  #  G0  #  G1  #  …  #  GK  #  Δ
```

- K se codifica en **binario** a la izquierda (como pide el enunciado). La cabeza comienza sobre su primer dígito.
- Cada generación nueva se escribe **a continuación** de la anterior, de modo que la cinta registra toda la
  evolución y la configuración final queda visible al final de la cinta.
- Al terminar, el contador vale 0 y la cabeza queda sobre el `#` que cierra la última generación.

### 3.2 Alfabeto

Σ = {0, 1, #, o, i, Δ}. Los símbolos `o` e `i` son marcas auxiliares: una celda 0 ó 1 de la generación actual
que ya fue procesada. Marcar en lugar de borrar conserva el valor de la celda, que todavía hace falta como
vecino izquierdo de la siguiente celda y como vecino derecho cíclico de la última.

### 3.3 Algoritmo

1. **Control de iteraciones.** Se resta 1 a K en binario: desde el dígito menos significativo, cada 0 se
   convierte en 1 (préstamo) hasta encontrar un 1, que pasa a 0. Si el préstamo llega al blanco, K era 0: se
   restauran los dígitos (que quedaron en 1) a 0 y la máquina se detiene.
2. **Ubicar la generación actual Gt.** Es el último bloque cerrado por `#`: se va al final de la cinta (`RΔ`),
   un lugar a la izquierda (el `#` de cierre de Gt) y luego `L#` hasta el `#` de apertura.
3. **Procesar cada celda** de Gt, de izquierda a derecha:
   1. `R{0,1,#}` busca la primera celda **sin marcar**: ésa es la celda central c. Se la marca (`o` o `i`).
   2. **Vecino izquierdo l:** `L`. Si se lee una marca, ése es l. Si se lee `#`, c era la primera celda y por la
      **frontera cíclica** l es la última celda de Gt (`R#` y luego `L`).
   3. **Volver al centro:** `R#` hasta el cierre de Gt y `L{o,i}`: el centro es la celda marcada más a la derecha.
   4. **Vecino derecho r:** `R`. Si se lee 0/1, ése es r. Si se lee `#`, c era la última celda y r es la primera
      de Gt (`L#` y luego `R`).
   5. **Escribir** regla(l, c, r) al final de la cinta (`RΔ` y el bloque `0` ó `1`), y volver con `L#`.

   Los valores l, c y r no se guardan en la cinta: quedan codificados en **la rama del diagrama** por la que
   avanza la máquina (2 ramas para c, 4 para (l, c) y finalmente 8 vecindarios que confluyen en las 2 ramas de
   escritura). Así la tabla de verdad de la regla es exactamente la elección de rama al leer r.
4. **Cerrar la generación.** Cuando `R{0,1,#}` encuentra el `#` de cierre, todas las celdas están marcadas: se
   recorre Gt hacia la izquierda reemplazando `o→0` e `i→1`, se escribe `#` al final de Gt+1 y se vuelve
   (`LΔ`) al contador.

### 3.4 Diagrama de bloques

El diagrama completo tiene 51 nodos (se puede ver en la pestaña *Máquina* de la aplicación y en
`docs/diagrama_bloques.md`). Resumido por fases (`*` = cualquier símbolo):

```
CONTADOR      INICIO: R#  ─*→  DEC: L  ─0→  DEC_1: 1 ─*→ DEC
                                     ─1→  DEC_0: 0 ─*→ GEN_FIN
                                     ─Δ→  CERO: R ─1→ CERO_W: 0 ─*→ CERO
                                                  ─#→ FIN: RΔ ─*→ FIN_L: L ─*→ ALTO

UBICAR Gt     GEN_FIN: RΔ ─*→ GEN_CIERRE: L ─*→ APERTURA: L# ─*→ SIG_CELDA: R{0,1,#}
              SIG_CELDA ─0→ MARCAR_C0   ─1→ MARCAR_C1   ─#→ DESMARCAR

VECINO IZQ.   MARCAR_Cc: o|i ─*→ IZQ_Cc: L ─o→ rama (l=0,c)  ─i→ rama (l=1,c)
                                          ─#→ CICLO_IZQ_Cc: R# ─*→ L ─0,o→ (l=0,c)  ─1,i→ (l=1,c)

VECINO DER.   IR_CIERRE_LlCc: R# ─*→ CENTRO: L{o,i} ─*→ DER: R ─0→ ESC_FIN_regla(l,c,0)
                                                            ─1→ ESC_FIN_regla(l,c,1)
                                                            ─#→ CICLO_DER: L# ─*→ R ─o→ ESC_FIN_regla(l,c,0)
                                                                                    ─i→ ESC_FIN_regla(l,c,1)

ESCRITURA     ESC_FIN_v: RΔ ─*→ ESCRIBIR_v: v ─*→ VOLVER: L# ─*→ APERTURA

CIERRE        DESMARCAR: L ─o→ 0 ─*→ DESMARCAR   ─i→ 1 ─*→ DESMARCAR
                           ─#→ CERRAR_FIN: RΔ ─*→ CERRAR: # ─*→ VOLVER_K: LΔ ─*→ INICIO
```

### 3.5 De los bloques a la tabla de transiciones

Cada bloque se traduce a transiciones estándar con un estado de entrada y uno de salida. Por ejemplo:

```
R:      δ(q0, σ) = (qf, σ, D)                      ∀σ ∈ Σ
x:      δ(q0, σ) = (qf, x, N)                      ∀σ ∈ Σ
Rx:     δ(q0, σ) = (q1, σ, D)                      ∀σ ∈ Σ   (primer movimiento)
        δ(q1, σ) = (q1, σ, D)   si σ ∉ X;   δ(q1, σ) = (qf, σ, N)   si σ ∈ X
SR/SL:  un estado "llevar[σ]" por cada símbolo no blanco transporta el símbolo una celda
```

Cada flecha `A ─σ→ B` del diagrama se convierte en `δ(salida_A, σ) = (entrada_B, σ, N)`. El resultado para
la Regla 110 es una única MT con:

| | |
|---|---|
| \|S\| | 127 estados (q0 … q125 y qH) |
| Σ | {0, 1, #, o, i, Δ} |
| s0 | q0 (nodo INICIO) |
| F | {qH} |
| \|δ\| | 705 transiciones |

La tabla completa, con el bloque de origen de cada estado, está en `docs/tabla_transiciones.md` y en la
aplicación (con filtro por estado o bloque). Si en algún momento δ no está definida para el par actual la
máquina se detiene de forma **anómala**, lo que sirve como detector de errores de diseño (nunca ocurre en las
pruebas).

### 3.6 Cambio de regla

Como la regla sólo decide a qué rama de escritura va cada uno de los 8 vecindarios, el mismo diseño sirve para
cualquiera de las 256 reglas elementales. La aplicación ofrece además las reglas 101, 30, 90, 54, 150 y 184
para comparar patrones.

## 4. Metodología

### 4.1 Arquitectura del software

Se usó Java 21 y JavaFX con una arquitectura MVC por capas (modelo, negocio, servicio, controlador, ui, dao),
siguiendo los principios SOLID:

| Principio | Aplicación |
|---|---|
| SRP | La máquina (`MaquinaTuring`) sólo describe (S, Σ, δ, s0, F); el `MotorTuring` la ejecuta; el `CompiladorDiagrama` la construye; los presentadores sólo traducen entre la vista y el coordinador. |
| OCP | Nuevos bloques implementan `BloqueTuring` sin tocar el compilador; nuevas reglas implementan `ReglaLocal` sin tocar el diseño de la MT. |
| LSP | `SimuladorDirecto` y `SimuladorMaquinaTuring` son intercambiables como `SimuladorAutomata`; todos los bloques se compilan igual. |
| ISP | Interfaces pequeñas por rol: `Cinta`, `MotorTuring`, `BloqueTuring`, `ReglaLocal`, `CasoPruebaDAO`. |
| DIP | El coordinador ensambla las dependencias; el DAO se elige por `factory.properties`; los servicios reciben el compilador y la fábrica de motores por constructor. |

### 4.2 Verificación

- **Pruebas unitarias de cada bloque** (`BloquesTuringTest`): R, L, x, Rx, Lx, SR y SL ejecutados como máquinas
  independientes.
- **Oráculo independiente:** `SimuladorDirecto` aplica la regla con un bucle común. La MT se compara contra él
  en entradas aleatorias (N de 1 a 20, K de 0 a 7) para las reglas 110, 101, 30, 90, 54, 184, 0 y 255.
- **Ejemplos del enunciado** y casos dirigidos a las fronteras cíclicas (vecindarios que sólo existen si los
  extremos son adyacentes: 111 en la celda 0 y 101 en la celda N−1).
- **Casos de prueba del archivo** `casos_prueba.txt`, cuyos valores esperados se calcularon por separado.

## 5. Resultados

Ejecución de `casos_prueba.txt` con la Regla 110 (generado con `mvn compile exec:java`):

| Línea | Entrada | Generación final (MT) | Pasos | Resultado |
|---|---|---|---|---|
| 6 | `100#000000010000000#` | `000111110000000` | 6.341 | ✔ Ejemplo 1 del enunciado |
| 7 | `1110#01101001#` | `01101110` | 10.317 | ✔ Ejemplo 2 del enunciado |
| 8 | `0#10110010#` | `10110010` | 29 | ✔ K = 0 |
| 9 | `1#11000001#` | `01000011` | 635 | ✔ Frontera cíclica izquierda |
| 10 | `1#10000010#` | `10000111` | 635 | ✔ Frontera cíclica derecha |
| 11 | `101#00000000#` | `00000000` | 3.292 | ✔ Todas muertas |
| 12 | `101#11111111#` | `00000000` | 3.292 | ✔ Todas vivas |
| 13 | `10#01010101#` | `00000000` | 1.277 | ✔ Patrón alternado |
| 14 | `0011#00100100#` | `10000101` | 1.921 | ✔ K con ceros a la izquierda |
| 15 | `1111#0000000000000001#` | `1101011001111101` | 28.949 | ✔ Crecimiento del triángulo |
| 16 | `11111#0…01#` (N=30) | `110110010011011100111000001101` | 177.661 | ✔ K=31, N=30 |
| 17 | `1100100#10011101011000101101#` | `11011111000111000100` | 450.280 | ✔ K=100, N=20 |
| 18 | `10000000#0001001101111100010011011111#` | `0111110001001101111100010011` | 1.017.451 | ✔ K=128, N=28 |
| 19 | `1#0101#` | — | — | ✔ rechazada (N < 8) |
| 20 | `12#00000000#` | — | — | ✔ rechazada (K no binario) |
| 21 | `101#0000000#0` | — | — | ✔ rechazada (formato) |

**16 de 16 casos correctos.** El ejemplo de la consigna con K = 3 (`000100 → 001100 → 011100 → 110100`) también
se verifica en las pruebas unitarias.

**Patrón triangular.** Con N = 101, K = 100 y una única celda viva en el extremo derecho, la MT ejecuta
5.480.461 pasos y produce el triángulo que crece hacia la izquierda, característico de la Regla 110, idéntico al
de la simulación directa.

**Costo.** Por cada celda la cabeza recorre Gt y la parte escrita de Gt+1 varias veces (O(N)), por lo que una
generación cuesta O(N²) pasos; volver al contador cuesta O(t·N). En total O(K·N² + K²·N) pasos, coherente con
los valores medidos.

## 6. Conclusiones

- Fue posible simular la Regla 110 con una MT de 127 estados construida sólo con bloques elementales. Diseñar
  con bloques y compilar automáticamente a δ permitió razonar sobre el algoritmo en un nivel alto sin perder la
  definición formal (S, Σ, δ, s0, F).
- La información que una MT "recuerda" puede vivir en la cinta (marcas `o`/`i`) o en el estado (las ramas del
  diagrama que codifican l, c y r). Combinar ambas mantiene la tabla pequeña.
- Las fronteras cíclicas son el punto delicado: los vecinos de los extremos están a distancia N en la cinta, y
  se resuelven con desvíos explícitos hacia el otro extremo de la generación.
- El contraste entre la MT (millones de pasos) y la simulación directa (un bucle) muestra el costo de la
  universalidad del modelo: la MT puede computarlo todo, pero no necesariamente de forma eficiente.
- La Regla 110 es a su vez Turing-completa, de modo que ambos modelos pueden simularse mutuamente.

## 7. Referencias

- Turing, A. M. (1936). *On Computable Numbers, with an Application to the Entscheidungsproblem*.
  Proceedings of the London Mathematical Society, 2(42), 230–265.
- Wolfram, S. (1983). *Statistical Mechanics of Cellular Automata*. Reviews of Modern Physics, 55(3), 601–644.
- Wolfram, S. (2002). *A New Kind of Science*. Wolfram Media.
- Cook, M. (2004). *Universality in Elementary Cellular Automata*. Complex Systems, 15(1), 1–40.
- Lewis, H. R., & Papadimitriou, C. H. (1998). *Elements of the Theory of Computation* (2.ª ed.). Prentice Hall.
- Hopcroft, J. E., Motwani, R., & Ullman, J. D. (2007). *Introducción a la teoría de autómatas, lenguajes y
  computación* (3.ª ed.). Pearson.
- Material de cátedra: *Gramáticas Sensibles al Contexto y Máquinas de Turing* (unidad 8).
- Fowler, M. (2018). *Refactoring: Improving the Design of Existing Code* (2.ª ed.). Addison-Wesley.
