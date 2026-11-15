# Documentación – Máquina de Turing que simula la Regla 110

**Trabajo Práctico Final – Fundamentos Teóricos de la Informática**
Autor: Francisco Agustin Molina · Versión 1.0

Este documento reúne en un solo lugar toda la información del proyecto: el problema, la máquina de Turing
diseñada, cómo se construye y se ejecuta, el software que la acompaña, las pruebas y los resultados.

---

## Índice

1. [Resumen](#1-resumen)
2. [Requisitos y ejecución](#2-requisitos-y-ejecución)
3. [Marco teórico](#3-marco-teórico)
4. [Especificación de la máquina](#4-especificación-de-la-máquina)
5. [Algoritmo](#5-algoritmo)
6. [Bloques de construcción](#6-bloques-de-construcción)
7. [Diagrama de bloques completo](#7-diagrama-de-bloques-completo)
8. [Compilación a la tabla δ](#8-compilación-a-la-tabla-δ)
9. [Ejecución de la máquina](#9-ejecución-de-la-máquina)
10. [Arquitectura del software](#10-arquitectura-del-software)
11. [Interfaz gráfica](#11-interfaz-gráfica)
12. [Configuración](#12-configuración)
13. [Casos de prueba](#13-casos-de-prueba)
14. [Pruebas automatizadas](#14-pruebas-automatizadas)
15. [Resultados](#15-resultados)
16. [Complejidad](#16-complejidad)
17. [Decisiones de diseño](#17-decisiones-de-diseño)
18. [Limitaciones conocidas](#18-limitaciones-conocidas)
19. [Archivos generados](#19-archivos-generados)
20. [Referencias](#20-referencias)

---

## 1. Resumen

Se construyó una **Máquina de Turing (MT)** que, dada una configuración inicial binaria de longitud N y un
número de generaciones K, calcula la evolución de un **autómata celular elemental según la Regla 110** con
**fronteras cíclicas**, y deja registradas en la cinta todas las generaciones.

La máquina se diseña componiendo los **bloques de construcción** de la cátedra (`R`, `L`, `x`, `Rx`, `Lx`, `SR`,
`SL`) en un diagrama, que un compilador traduce a una MT estándar con una única tabla δ.

| Dato | Valor |
|---|---|
| Nodos del diagrama | 51 |
| Estados \|S\| | 127 (q0 … q125 y qH) |
| Alfabeto Σ | {0, 1, #, o, i, Δ} |
| Transiciones \|δ\| | 705 |
| Estado inicial s0 | q0 (nodo `INICIO`) |
| Estados finales F | {qH} |
| Casos de prueba del archivo | 16 de 16 correctos |

Junto con la máquina hay una aplicación Java/JavaFX que la ejecuta paso a paso, muestra la cinta, la tabla δ y
el diagrama espacio-tiempo del autómata, donde aparece el patrón triangular característico de la regla.

---

## 2. Requisitos y ejecución

### Requisitos

| Herramienta | Versión |
|---|---|
| JDK | 21 o superior (en Fedora 44: `sudo dnf install java-25-openjdk-devel`) |
| Maven | 3.9+ (`sudo dnf install maven`) |

Maven descarga el resto de las dependencias:

| Dependencia | Versión | Uso |
|---|---|---|
| JavaFX (`javafx-controls`) | 21.0.5 | Interfaz gráfica |
| Log4j 2 (`log4j-api`, `log4j-core`) | 2.24.1 | Registro de eventos |
| JUnit Jupiter | 5.11.3 | Pruebas |

Plugins: `maven-compiler-plugin` 3.13.0 (release 21), `maven-surefire-plugin` 3.5.2, `javafx-maven-plugin`
0.0.8, `exec-maven-plugin` 3.5.0.

### Comandos

```bash
mvn javafx:run            # interfaz gráfica (clase principal turing.aplicacion.AplicacionPrincipal)
mvn test                  # pruebas: bloques, máquina vs. simulación directa, casos del .txt
mvn compile exec:java     # genera docs/diagrama_bloques.md, docs/tabla_transiciones.md y docs/resultados_casos.md
```

`exec:java` ejecuta `turing.aplicacion.ExportadorTabla`, que compila la máquina, corre los casos de prueba y
escribe los tres documentos de `docs/`.

---

## 3. Marco teórico

### 3.1 Máquina de Turing

Siguiendo la definición de la cátedra, una MT es **T = (S, Σ, δ, s0, F)**:

- S: conjunto finito de estados.
- Σ: alfabeto de trabajo, con Δ ∈ Σ (blanco).
- δ : S × Σ → S × Σ × {I, D, N}: función de transición **parcial**.
- s0: estado inicial.
- F: conjunto de estados de parada.

La cinta es infinita en ambas direcciones y la cabeza comienza sobre el primer carácter de la entrada.

### 3.2 Máquinas simplificadas (bloques)

Las máquinas complejas se diseñan combinando máquinas elementales en un diagrama. Las flechas entre bloques se
etiquetan con el símbolo leído al terminar el bloque anterior.

| Bloque | Comportamiento |
|---|---|
| `R` | Se mueve un lugar a la derecha sin importar el símbolo leído. |
| `L` | Se mueve un lugar a la izquierda sin importar el símbolo leído. |
| `x` | Escribe x en la celda actual (una máquina por símbolo). |
| `Rx` | Se desplaza a la derecha hasta la primera x. |
| `Lx` | Se desplaza a la izquierda hasta la primera x. |
| `SR` | Corre un lugar a la derecha la cadena de la izquierda, desde la posición actual hasta el primer blanco. |
| `SL` | Corre un lugar a la izquierda la cadena de la derecha, desde la posición actual hasta el primer blanco. |

### 3.3 Autómatas celulares elementales y la Regla 110

Un autómata celular elemental es una hilera de celdas binarias que evoluciona en tiempo discreto: el nuevo valor
de cada celda depende sólo de ella y de sus dos vecinas (radio 1). Hay 2⁸ = 256 reglas; Wolfram las numera
leyendo la tabla de verdad como un número binario. La Regla 110 (0110 1110₂):

| Patrón (l c r) | 111 | 110 | 101 | 100 | 011 | 010 | 001 | 000 |
|---|---|---|---|---|---|---|---|---|
| Nuevo estado | 0 | 1 | 1 | 0 | 1 | 1 | 1 | 0 |

Con **fronteras cíclicas**, la celda 0 tiene como vecino izquierdo a la celda N−1, y la celda N−1 tiene como
vecino derecho a la celda 0.

Matthew Cook demostró (2004) que la Regla 110 es **Turing-completa**. En este trabajo se recorre el camino
inverso: una MT simula a la Regla 110.

---

## 4. Especificación de la máquina

### 4.1 Formato de la cinta

```
entrada:   Δ  K  #  G0  #  Δ                    ej.  100#000000010000000#   (K = 4, N = 15)
salida:    Δ  0…0  #  G0  #  G1  #  …  #  GK  #  Δ
```

- **K** se codifica en **binario** a la izquierda. Se admiten ceros a la izquierda (`0011` = 3).
- **G0** es la configuración inicial: N celdas `0`/`1`, con **N ≥ 8**.
- La cabeza comienza sobre el **primer dígito de K**.
- Cada generación nueva se escribe **a continuación** de la anterior y se cierra con `#`.
- Al terminar, el contador vale `0…0` (misma cantidad de dígitos) y la cabeza queda sobre el `#` que cierra GK.

### 4.2 Alfabeto

| Símbolo | Significado |
|---|---|
| `0` | Celda muerta / dígito binario |
| `1` | Celda viva / dígito binario |
| `#` | Separador entre el contador y cada generación |
| `o` | Celda `0` ya procesada (marca temporal) |
| `i` | Celda `1` ya procesada (marca temporal) |
| `Δ` | Blanco |

Las marcas `o`/`i` sólo existen mientras se calcula una generación; al cerrarla se restauran a `0`/`1`.

### 4.3 Validación de la entrada

La entrada se valida en el software antes de ejecutar la máquina (`ValidadorEntrada`):

| Regla | Mensaje |
|---|---|
| No vacía | «La cinta está vacía.» |
| Formato `[01]+#[01]+#` | «Formato inválido. Se espera K#celdas# con K binario y celdas 0/1, por ejemplo 100#000000010000000#» |
| K de hasta 20 bits | «K tiene demasiados dígitos (máximo 20 bits).» |
| N ≥ 8 | «La hilera debe tener longitud N ≥ 8 (tiene n).» |

---

## 5. Algoritmo

1. **Control de iteraciones.** Se resta 1 a K en binario: desde el dígito menos significativo, cada `0` se
   convierte en `1` (préstamo) hasta encontrar un `1`, que pasa a `0`. Si el préstamo llega al blanco, K era 0:
   se restauran los dígitos (que quedaron en `1`) a `0`, la cabeza va al último `#` y la máquina se detiene.
2. **Ubicar la generación actual Gt.** Es el último tramo cerrado por `#`: se va al final de la cinta (`RΔ`), un
   lugar a la izquierda (el `#` de cierre de Gt) y luego `L#` hasta el `#` de apertura.
3. **Procesar cada celda** de Gt, de izquierda a derecha:
   1. `R{0,1,#}` busca la primera celda **sin marcar**: es la celda central c. Se la marca (`o` o `i`).
   2. **Vecino izquierdo l:** `L`. Si se lee una marca, ése es l. Si se lee `#`, c era la primera celda y, por la
      frontera cíclica, l es la última celda de Gt (`R#` y luego `L`).
   3. **Volver al centro:** `R#` hasta el cierre de Gt y `L{o,i}`: el centro es la celda marcada más a la derecha.
   4. **Vecino derecho r:** `R`. Si se lee `0`/`1`, ése es r. Si se lee `#`, c era la última celda y r es la
      primera de Gt (`L#` y luego `R`).
   5. **Escribir** regla(l, c, r) al final de la cinta (`RΔ` y el bloque `0` o `1`) y volver con `L#`.
4. **Cerrar la generación.** Cuando `R{0,1,#}` encuentra el `#` de cierre, todas las celdas están marcadas: se
   recorre Gt hacia la izquierda reemplazando `o→0` e `i→1`, se escribe `#` al final de Gt+1 y se vuelve (`LΔ`)
   al contador. Se repite desde el paso 1.

**l, c y r no se guardan en la cinta**: quedan codificados en la rama del diagrama por la que avanza la máquina
(2 ramas según c, 4 según (l, c) y, al leer r, los 8 vecindarios confluyen en las 2 ramas de escritura). La tabla
de verdad de la regla es exactamente la elección de rama al leer r.

### Ejemplo (K = 1, N = 8)

```
inicio:   1#11000001#
contador: 0#11000001#            (1 → 0, hay que calcular una generación)
celda 0:  0#i1000001#0           (l = última = 1, c = 1, r = 1 → 111 → 0)
celda 1:  0#ii000001#01          (l = 1, c = 1, r = 0 → 110 → 1)
…
cierre:   0#11000001#01000011#
contador: préstamo llega al blanco → restaura → alto sobre el último #
```

---

## 6. Bloques de construcción

Bloques implementados (`FabricaBloques` los crea a partir de su notación): `L`, `R`, `x`, `Rx`, `Lx`, `SR`, `SL`.

| Notación | Clase | Jerarquía | Notas |
|---|---|---|---|
| `R` | `MoverDerecha` | `BloqueMovimiento` | |
| `L` | `MoverIzquierda` | `BloqueMovimiento` | |
| `x` | `Escribir` | `BloqueTuring` | Una instancia por símbolo; no mueve la cabeza |
| `Rx` | `BuscarDerecha` | `BloqueBusqueda` | Acepta un símbolo o un conjunto (`R{0,1,#}`) |
| `Lx` | `BuscarIzquierda` | `BloqueBusqueda` | Acepta un símbolo o un conjunto (`L{o,i}`) |
| `SR` | `DesplazarDerecha` | `BloqueDesplazamiento` | Implementado y probado; no se usa en el diagrama |
| `SL` | `DesplazarIzquierda` | `BloqueDesplazamiento` | Implementado y probado; no se usa en el diagrama |

Cada bloque se compila a un `FragmentoMaquina(entrada, salida, transiciones)`:

```
R:      δ(q0, σ) = (qf, σ, D)                                     ∀σ ∈ Σ
L:      δ(q0, σ) = (qf, σ, I)                                     ∀σ ∈ Σ
x:      δ(q0, σ) = (qf, x, N)                                     ∀σ ∈ Σ
Rx:     δ(q0, σ) = (q1, σ, D)                                     ∀σ ∈ Σ   (primer movimiento)
        δ(q1, σ) = (q1, σ, D)  si σ ∉ X;   δ(q1, σ) = (qf, σ, N)  si σ ∈ X
Lx:     igual que Rx con movimiento I
SR/SL:  un estado "llevar[σ]" por cada símbolo no blanco transporta el símbolo una celda
```

`Rx` y `Lx` siguen la semántica de la cátedra: **primero se mueven y después buscan**, por eso no se detienen
sobre la celda de partida aunque contenga el símbolo buscado.

Los estados se numeran con `GeneradorEstados` (prefijo `q`) y cada uno guarda la etiqueta del bloque de origen
(por ejemplo `INICIO · R#`), que aparece en la tabla δ.

---

## 7. Diagrama de bloques completo

Nodo inicial: `INICIO`. `*` = cualquier símbolo. `ALTO` = estado de parada qH.

### 7.1 Vista por fases

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

### 7.2 Tabla de nodos

| Nodo | Bloque | Flechas (símbolo → destino) | Función |
|---|---|---|---|
| `INICIO` | `R#` | `*` → `DEC` | Desde el contador K avanza hasta el primer # |
| `DEC` | `L` | `0` → `DEC_1`<br>`1` → `DEC_0`<br>`Δ` → `CERO` | Se para sobre el dígito menos significativo pendiente de K |
| `DEC_1` | `1` | `*` → `DEC` | Préstamo: 0 → 1 y sigue a la izquierda |
| `DEC_0` | `0` | `*` → `GEN_FIN` | 1 → 0: el decremento terminó, hay que calcular otra generación |
| `CERO` | `R` | `1` → `CERO_W`<br>`#` → `FIN` | K era 0: recorre los dígitos para restaurarlos |
| `CERO_W` | `0` | `*` → `CERO` | Restaura un dígito a 0 |
| `FIN` | `RΔ` | `*` → `FIN_L` | Va al final de la cinta |
| `FIN_L` | `L` | `*` → `ALTO` | Queda sobre el # que cierra la última generación |
| `GEN_FIN` | `RΔ` | `*` → `GEN_CIERRE` | Va al final de la cinta |
| `GEN_CIERRE` | `L` | `*` → `APERTURA` | Queda sobre el # que cierra Gt |
| `APERTURA` | `L#` | `*` → `SIG_CELDA` | Va al # que abre Gt |
| `SIG_CELDA` | `R{0,1,#}` | `0` → `MARCAR_C0`<br>`1` → `MARCAR_C1`<br>`#` → `DESMARCAR` | Busca la primera celda sin marcar de Gt (o el # de cierre) |
| `VOLVER` | `L#` | `*` → `APERTURA` | Tras escribir una celda de Gt+1 vuelve al # que cierra Gt |
| `MARCAR_C0` | `o` | `*` → `IZQ_C0` | Marca la celda central c=0 |
| `IZQ_C0` | `L` | `o` → `IR_CIERRE_L0C0`<br>`i` → `IR_CIERRE_L1C0`<br>`#` → `CICLO_IZQ_C0` | Mira la celda de la izquierda (c=0) |
| `CICLO_IZQ_C0` | `R#` | `*` → `CICLO_IZQ_L_C0` | Frontera cíclica: va al # de cierre de Gt (c=0) |
| `CICLO_IZQ_L_C0` | `L` | `0,o` → `IR_CIERRE_L0C0`<br>`1,i` → `IR_CIERRE_L1C0` | Lee la última celda de Gt como vecino izquierdo (c=0) |
| `IR_CIERRE_L0C0` | `R#` | `*` → `CENTRO_L0C0` | Conoce l=0, c=0: va al # de cierre de Gt |
| `CENTRO_L0C0` | `L{o,i}` | `*` → `DER_L0C0` | Vuelve al centro: la celda marcada más a la derecha |
| `DER_L0C0` | `R` | `0` → `ESC_FIN_0`<br>`1` → `ESC_FIN_1`<br>`#` → `CICLO_DER_L0C0` | Mira la celda de la derecha |
| `CICLO_DER_L0C0` | `L#` | `*` → `CICLO_DER_R_L0C0` | Frontera cíclica: va al # de apertura de Gt |
| `CICLO_DER_R_L0C0` | `R` | `o` → `ESC_FIN_0`<br>`i` → `ESC_FIN_1` | Lee la primera celda de Gt como vecino derecho |
| `IR_CIERRE_L1C0` | `R#` | `*` → `CENTRO_L1C0` | Conoce l=1, c=0: va al # de cierre de Gt |
| `CENTRO_L1C0` | `L{o,i}` | `*` → `DER_L1C0` | Vuelve al centro |
| `DER_L1C0` | `R` | `0` → `ESC_FIN_0`<br>`1` → `ESC_FIN_1`<br>`#` → `CICLO_DER_L1C0` | Mira la celda de la derecha |
| `CICLO_DER_L1C0` | `L#` | `*` → `CICLO_DER_R_L1C0` | Frontera cíclica: va al # de apertura de Gt |
| `CICLO_DER_R_L1C0` | `R` | `o` → `ESC_FIN_0`<br>`i` → `ESC_FIN_1` | Lee la primera celda de Gt como vecino derecho |
| `MARCAR_C1` | `i` | `*` → `IZQ_C1` | Marca la celda central c=1 |
| `IZQ_C1` | `L` | `o` → `IR_CIERRE_L0C1`<br>`i` → `IR_CIERRE_L1C1`<br>`#` → `CICLO_IZQ_C1` | Mira la celda de la izquierda (c=1) |
| `CICLO_IZQ_C1` | `R#` | `*` → `CICLO_IZQ_L_C1` | Frontera cíclica: va al # de cierre de Gt (c=1) |
| `CICLO_IZQ_L_C1` | `L` | `0,o` → `IR_CIERRE_L0C1`<br>`1,i` → `IR_CIERRE_L1C1` | Lee la última celda de Gt como vecino izquierdo (c=1) |
| `IR_CIERRE_L0C1` | `R#` | `*` → `CENTRO_L0C1` | Conoce l=0, c=1: va al # de cierre de Gt |
| `CENTRO_L0C1` | `L{o,i}` | `*` → `DER_L0C1` | Vuelve al centro |
| `DER_L0C1` | `R` | `0` → `ESC_FIN_1`<br>`1` → `ESC_FIN_1`<br>`#` → `CICLO_DER_L0C1` | Mira la celda de la derecha |
| `CICLO_DER_L0C1` | `L#` | `*` → `CICLO_DER_R_L0C1` | Frontera cíclica: va al # de apertura de Gt |
| `CICLO_DER_R_L0C1` | `R` | `o` → `ESC_FIN_1`<br>`i` → `ESC_FIN_1` | Lee la primera celda de Gt como vecino derecho |
| `IR_CIERRE_L1C1` | `R#` | `*` → `CENTRO_L1C1` | Conoce l=1, c=1: va al # de cierre de Gt |
| `CENTRO_L1C1` | `L{o,i}` | `*` → `DER_L1C1` | Vuelve al centro |
| `DER_L1C1` | `R` | `0` → `ESC_FIN_1`<br>`1` → `ESC_FIN_0`<br>`#` → `CICLO_DER_L1C1` | Mira la celda de la derecha |
| `CICLO_DER_L1C1` | `L#` | `*` → `CICLO_DER_R_L1C1` | Frontera cíclica: va al # de apertura de Gt |
| `CICLO_DER_R_L1C1` | `R` | `o` → `ESC_FIN_1`<br>`i` → `ESC_FIN_0` | Lee la primera celda de Gt como vecino derecho |
| `ESC_FIN_0` | `RΔ` | `*` → `ESCRIBIR_0` | Va al final de la cinta para agregar la nueva celda |
| `ESCRIBIR_0` | `0` | `*` → `VOLVER` | Escribe regla(l,c,r) = 0 |
| `ESC_FIN_1` | `RΔ` | `*` → `ESCRIBIR_1` | Va al final de la cinta para agregar la nueva celda |
| `ESCRIBIR_1` | `1` | `*` → `VOLVER` | Escribe regla(l,c,r) = 1 |
| `DESMARCAR` | `L` | `o` → `DESMARCAR_0`<br>`i` → `DESMARCAR_1`<br>`#` → `CERRAR_FIN` | Recorre Gt hacia la izquierda quitando las marcas |
| `DESMARCAR_0` | `0` | `*` → `DESMARCAR` | o → 0 |
| `DESMARCAR_1` | `1` | `*` → `DESMARCAR` | i → 1 |
| `CERRAR_FIN` | `RΔ` | `*` → `CERRAR` | Va al final de Gt+1 |
| `CERRAR` | `#` | `*` → `VOLVER_K` | Cierra Gt+1 con # |
| `VOLVER_K` | `LΔ` | `*` → `INICIO` | Vuelve al blanco que precede al contador K |

Las flechas que salen de `DER_*` y `CICLO_DER_R_*` codifican la Regla 110: por ejemplo, `DER_L1C1` con r=1
(vecindario 111) va a `ESC_FIN_0`, y con r=0 (110) va a `ESC_FIN_1`.

---

## 8. Compilación a la tabla δ

`CompiladorDiagrama` traduce el diagrama en dos fases:

1. **Nodos:** cada nodo se compila a su fragmento (entrada, salida, transiciones).
2. **Flechas:** cada flecha `A ─σ→ B` se convierte en una transición sin movimiento:
   ```
   δ(salida_A, σ) = (entrada_B, σ, N)     para cada σ que acepta la flecha
   ```
   Las flechas hacia `ALTO` apuntan al estado `qH`. Si dos flechas aceptan el mismo símbolo, gana la primera
   declarada (la máquina sigue siendo determinista).

Antes de compilar, `DiagramaBloques.validar()` verifica la consistencia del diagrama.

Resultado para la Regla 110:

| | |
|---|---|
| \|S\| | 127 estados (q0 … q125 y qH) |
| Σ | {0, 1, #, o, i, Δ} |
| s0 | q0 (nodo `INICIO`) |
| F | {qH} |
| \|δ\| | 705 transiciones |

Primeras filas de la tabla (la tabla completa está en [`tabla_transiciones.md`](tabla_transiciones.md)):

| Estado | Lee | Nuevo estado | Escribe | Mov | Bloque |
|---|---|---|---|---|---|
| q0 | 0 | q1 | 0 | D | INICIO · R# |
| q0 | # | q1 | # | D | INICIO · R# |
| q1 | 0 | q1 | 0 | D | INICIO · R# |
| q1 | # | q2 | # | N | INICIO · R# |
| q3 | * | q4 | = | I | DEC · L |
| q5 | * | q6 | 1 | N | DEC_1 · 1 |
| q7 | * | q8 | 0 | N | DEC_0 · 0 |

(`*` = cualquier símbolo de Σ, `=` = reescribe el mismo símbolo.)

δ es **parcial**: si no está definida para el par actual, la máquina se detiene de forma anómala. Esto sirve
como detector de errores de diseño; no ocurre en ninguna prueba.

---

## 9. Ejecución de la máquina

### 9.1 Motor

`MotorTuringSecuencial` ejecuta una transición por paso: lee el símbolo, consulta δ, escribe, mueve la cabeza y
cambia de estado. Después de cada paso calcula el resultado:

| `ResultadoEjecucion` | Descripción |
|---|---|
| `EN_EJECUCION` | En ejecución |
| `DETENIDA_EN_ESTADO_FINAL` | Detenida en estado final (qH) |
| `DETENIDA_SIN_TRANSICION` | Detenida sin transición (anómala) |
| `LIMITE_ALCANZADO` | Límite de pasos alcanzado (200.000.000 por defecto) |

### 9.2 Cinta

`CintaInfinita` modela la cinta infinita en ambas direcciones con un arreglo de `char` que **duplica su tamaño**
hacia el lado necesario cuando la cabeza sale de los límites. La posición lógica 0 es el primer carácter de la
entrada y la cabeza empieza ahí. Fuera del arreglo se lee blanco.

### 9.3 Codificación

`CodificadorCinta` traduce entre los datos del autómata y la cinta:

- `codificar(EntradaAutomata(K=4, 000000010000000))` → `"100#000000010000000#"`.
- `decodificar(cinta)` valida y devuelve `EntradaAutomata`.
- `leerEvolucion(contenido)` interpreta una cinta completa **o a medio procesar**: las marcas `o`/`i` se leen como
  `0`/`1` y lo que está después del último `#` es la generación parcial.

### 9.4 Simuladores

Ambos implementan `SimuladorAutomata` y son intercambiables:

| Simulador | Cómo calcula |
|---|---|
| `SimuladorMaquinaTuring` | Ejecuta la MT compilada y lee la evolución de la cinta |
| `SimuladorDirecto` | Aplica la regla con un bucle común (oráculo de verificación) |

---

## 10. Arquitectura del software

Java 21, JavaFX y arquitectura MVC por capas.

```
src/main/java/turing/
├── aplicacion/    AplicacionPrincipal (main), Constantes, ExportadorTabla (genera docs/)
├── conexion/      Factory (instancia el DAO configurado en factory.properties)
├── controlador/   CoordinadorApp: ensambla servicios y vista
├── dao/           CasoPruebaDAO + secuencial/CasoPruebaDAOSecuencial (lee casos_prueba.txt)
├── modelo/        Cinta, CintaInfinita, Estado, Transicion, Movimiento, Alfabeto, MaquinaTuring, CasoPrueba
├── negocio/
│   ├── bloques/   BloqueTuring, bloques R, L, x, Rx, Lx, SR, SL, FabricaBloques, GeneradorEstados, FragmentoMaquina
│   ├── diagrama/  DiagramaBloques, NodoDiagrama, Arco, Condicion, CompiladorDiagrama (→ tabla δ)
│   ├── automata/  DiagramaAutomataCelular (diseño de la MT), CodificadorCinta, ValidadorEntrada,
│   │              EntradaAutomata, EvolucionAutomata, SimuladorAutomata, SimuladorDirecto, SimuladorMaquinaTuring
│   ├── motor/     MotorTuring, MotorTuringSecuencial, ResultadoEjecucion
│   └── regla/     ReglaLocal, Regla110
├── servicio/      MaquinaService, CasoPruebaService (+ Impl), ResultadoCaso
└── ui/            Interfaz, JavaFXLauncher, InterfazJavaFXImpl, EjecutorAnimado, PresentadorSimulacion y componentes
```

### 10.1 Modelo

| Clase | Tipo | Responsabilidad |
|---|---|---|
| `MaquinaTuring` | clase final | Describe (S, Σ, δ, s0, F); consulta `transicion(estado, símbolo)` |
| `Estado` | record | Nombre y bloque de origen |
| `Transicion` | record | Origen, símbolo leído, destino, símbolo escrito, movimiento |
| `Movimiento` | enum | `IZQUIERDA (I, −1)`, `DERECHA (D, +1)`, `NINGUNO (N, 0)` |
| `Alfabeto` | clase final | Símbolos y blanco |
| `Cinta` / `CintaInfinita` | interfaz / clase | Leer, escribir, mover, contenido |
| `CasoPrueba` | record | Número de línea, entrada, esperada, descripción |

### 10.2 Negocio

- **`ReglaLocal`**: `siguiente(l, c, r)` y `getNombre()`. `Regla110` usa una tabla indexada por `l·4 + c·2 + r`.
- **`DiagramaAutomataCelular`**: construye el diagrama de la sección 7 a partir de una `ReglaLocal`.
- **`CompiladorDiagrama`**: diagrama → `MaquinaTuring`.
- **`MotorTuring`**: ejecución paso a paso o en lote (`ejecutar(maximoPasos)`).

### 10.3 Servicios

| Servicio | Operaciones |
|---|---|
| `MaquinaService` | `obtenerDiagrama(regla)`, `obtenerMaquina(regla)`, `nuevoMotor()` |
| `CasoPruebaService` | `buscarTodos()`, `ejecutar(caso, regla)` → `ResultadoCaso` |

`ResultadoCaso` guarda la salida de la MT, la de la simulación directa y la cantidad de pasos. Un caso es
correcto si la MT coincide con lo esperado (o si la entrada es rechazada cuando se espera `ERROR`).

### 10.4 Controlador y arranque

- `AplicacionPrincipal.main` crea el `CoordinadorApp` y llama a `iniciarAplicacion()`.
- `CoordinadorApp` inicializa los servicios (obtiene el DAO por `Factory`), crea la regla, se inyecta en la
  interfaz y la lanza. Es el único punto de contacto entre la vista y los servicios.
- `JavaFXLauncher` es un "humble launcher": recibe la `InterfazJavaFXImpl` por una referencia estática y le
  delega el `start(Stage)`.

### 10.5 Principios SOLID

| Principio | Aplicación |
|---|---|
| SRP | `MaquinaTuring` describe, `CompiladorDiagrama` construye, `MotorTuring` ejecuta, `PresentadorSimulacion` traduce entre vista y coordinador |
| OCP | Nuevos bloques implementan `BloqueTuring` sin tocar el compilador; la regla se inyecta como `ReglaLocal` |
| LSP | `SimuladorDirecto` y `SimuladorMaquinaTuring` son intercambiables; todos los bloques se compilan igual |
| ISP | Interfaces pequeñas por rol: `Cinta`, `MotorTuring`, `BloqueTuring`, `ReglaLocal`, `CasoPruebaDAO` |
| DIP | El coordinador ensambla dependencias; el DAO se elige por configuración; los servicios reciben compilador y fábrica de motores por constructor |

---

## 11. Interfaz gráfica

Una única pantalla de simulación (`PanelSimulacion` + `PresentadorSimulacion`):

- **Cinta:** campo de texto y botón «Cargar cinta». Errores de validación en un mensaje debajo.
- **Estado de la ejecución:** estado actual, bloque de origen, pasos, contador K, generación, última transición
  aplicada y resultado.
- **Controles:** «⟲ Reiniciar», «Paso», «▶ Reproducir», «Siguiente generación», «Hasta el final» y un control de
  velocidad.
- **Vista de la cinta** (`VistaCinta`) con la cabeza y el estado.
- **Diagrama espacio-tiempo** (`LienzoEvolucion`), «leído de la cinta»: se forma a partir del contenido real de
  la cinta, incluida la generación parcial.
- **Tabla δ** (`TablaTransicionesVista`) con la transición aplicada resaltada y filtro por estado o bloque.

Las marcas temporales se dibujan en la cinta como una **X** cuyo color indica el tipo: una para el `0` procesado
(`o`) y otra para el `1` procesado (`i`).

`EjecutorAnimado` usa un `AnimationTimer` de JavaFX y ejecuta como máximo **400.000 pasos por cuadro** para no
congelar la interfaz; la velocidad por defecto es 20 pasos por segundo. Estilos en `estilos.css` y colores en
`Paleta`.

---

## 12. Configuración

| Archivo | Contenido |
|---|---|
| `factory.properties` | `CASO_PRUEBA=turing.dao.secuencial.CasoPruebaDAOSecuencial` (implementación del DAO) |
| `secuencial.properties` | `casos_prueba=casos_prueba.txt` (se busca primero en el directorio de trabajo y luego en el classpath) |
| `log4j2.xml` | Consola (nivel WARN) y archivo `turing-app.log` (nivel INFO) |
| `estilos.css` | Estilos de la interfaz |
| `Constantes.java` | Símbolos de Σ, `LONGITUD_MINIMA = 8`, `LIMITE_PASOS = 200_000_000` |

`Factory` instancia por reflexión la clase indicada en `factory.properties` y la cachea.

---

## 13. Casos de prueba

Archivo `casos_prueba.txt` (copia en `src/main/resources/`). Un caso por línea:

```
entrada ; generación final esperada ; descripción
```

Las líneas que empiezan con `//` son comentarios. `ERROR` indica que la entrada debe ser rechazada. Los valores
esperados se calcularon aparte aplicando la tabla de la Regla 110 con fronteras cíclicas.

| Línea | Entrada | Esperada | Descripción |
|---|---|---|---|
| 6 | `100#000000010000000#` | `000111110000000` | Ejemplo 1 del enunciado (K=4, N=15) |
| 7 | `1110#01101001#` | `01101110` | Ejemplo 2 del enunciado (K=14, N=8) |
| 8 | `0#10110010#` | `10110010` | K=0: no se calcula ninguna generación |
| 9 | `1#11000001#` | `01000011` | Frontera cíclica izquierda: la celda 0 ve 111 usando la última celda |
| 10 | `1#10000010#` | `10000111` | Frontera cíclica derecha: la última celda ve 101 usando la primera |
| 11 | `101#00000000#` | `00000000` | Todas muertas: 000 → 0, configuración estable |
| 12 | `101#11111111#` | `00000000` | Todas vivas: 111 → 0 |
| 13 | `10#01010101#` | `00000000` | Patrón alternado |
| 14 | `0011#00100100#` | `10000101` | K con ceros a la izquierda (K=3) |
| 15 | `1111#0000000000000001#` | `1101011001111101` | Una celda viva en el borde derecho: crecimiento del triángulo |
| 16 | `11111#000000000000000000000000000001#` | `110110010011011100111000001101` | Triángulo con N=30 y K=31 |
| 17 | `1100100#10011101011000101101#` | `11011111000111000100` | K=100 sobre N=20 |
| 18 | `10000000#0001001101111100010011011111#` | `0111110001001101111100010011` | K=128 sobre N=28 (contador de 8 bits) |
| 19 | `1#0101#` | `ERROR` | N < 8 |
| 20 | `12#00000000#` | `ERROR` | K no es binario |
| 21 | `101#0000000#0` | `ERROR` | Formato incorrecto |

---

## 14. Pruebas automatizadas

`mvn test` ejecuta cuatro clases de prueba:

### `BloquesTuringTest` – cada bloque como máquina independiente

- `R` mueve un lugar a la derecha; `L` uno a la izquierda.
- `x` escribe sin moverse.
- `Rx` busca a la derecha moviéndose al menos una vez; con un conjunto se detiene en cualquiera de los símbolos.
- `Lx` busca a la izquierda.
- `SR` corre la cadena de la izquierda un lugar a la derecha; sobre blanco no hace nada.
- `SL` corre la cadena de la derecha un lugar a la izquierda, sólo hasta el primer blanco.
- La fábrica rechaza notaciones inválidas; notación correcta de cada bloque.

### `MaquinaAutomataTest` – la máquina completa

- Tabla de verdad de la Regla 110 (los 8 vecindarios).
- Ejemplo de la consigna con K = 3: `000100 → 001100 → 011100 → 110100`.
- La cinta final registra todas las generaciones y el contador queda en 0.
- K = 0 no calcula nada.
- Fronteras cíclicas.
- Los ejemplos del enunciado coinciden con la simulación directa.
- **100 entradas aleatorias** (semilla 110, N de 1 a 20, K de 0 a 7): la MT coincide con la simulación directa.
- La máquina es determinista y sólo usa símbolos del alfabeto.

### `CodificadorCintaTest`

- Decodifica los ejemplos del enunciado.
- Rechaza entradas mal formadas.
- Lee una cinta a medio procesar.

### `CasosPruebaArchivoTest`

- Todos los casos de `casos_prueba.txt` son correctos.

---

## 15. Resultados

Ejecución de `casos_prueba.txt` (generado con `mvn compile exec:java`):

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
| 19 | `1#0101#` | — | — | ✔ Rechazada: N < 8 |
| 20 | `12#00000000#` | — | — | ✔ Rechazada: K no binario |
| 21 | `101#0000000#0` | — | — | ✔ Rechazada: formato |

**16 de 16 casos correctos.**

**Patrón triangular.** Con N = 101, K = 100 y una única celda viva en el extremo derecho, la MT ejecuta
5.480.461 pasos y produce el triángulo que crece hacia la izquierda, idéntico al de la simulación directa.

---

## 16. Complejidad

- Por cada celda, la cabeza recorre Gt y la parte ya escrita de Gt+1 varias veces: O(N) por celda.
- Una generación cuesta **O(N²)** pasos.
- Volver al contador cuesta O(t·N) en la generación t, porque la cinta conserva el historial.
- Total: **O(K·N² + K²·N)** pasos, coherente con los valores medidos.
- Espacio: la cinta crece hasta |K| + (K+1)·(N+1) + 1 celdas.

---

## 17. Decisiones de diseño

El detalle está en [`DECISIONES.md`](DECISIONES.md). Resumen:

| Tensión | Se eligió | Se resignó |
|---|---|---|
| Tabla a mano vs. bloques compilados | Bloques compilados | Minimalidad de la máquina |
| Recordar en la cinta vs. en el estado | Avance en la cinta (marcas), vecindario en el estado (ramas) | Menos estados |
| Sobrescribir vs. conservar generaciones | Conservar todas | Tiempo (término K²·N) y espacio |
| Borrar vs. marcar celdas | Marcar con `o`/`i` | Dos símbolos más en Σ |
| Celdas fantasma vs. desvíos en los bordes | Desvíos explícitos | Recorridos extra en los extremos |
| δ total vs. δ parcial | Parcial, con parada anómala como detector de errores | — |
| Validar en la MT vs. en el software | En el software | La MT no reconoce el formato de entrada |
| Contar hacia arriba vs. decrementar K | Decrementar (la parada sale del préstamo) | K no se conserva en la salida |

Otras decisiones:

- La Regla 110 no se "calcula": está cableada en la topología del diagrama (elección de rama al leer r).
- El centro se reencuentra por invariante (la celda marcada más a la derecha), sin marcador extra.
- Los documentos de `docs/` se generan desde el mismo diagrama que se ejecuta, así no se desactualizan.
- El patrón de la interfaz se lee de la cinta real, no de una simulación paralela.

---

## 18. Limitaciones conocidas

- K está limitado a 20 bits y la ejecución a 200 millones de pasos.
- La MT asume una entrada bien formada; la validación ocurre fuera de ella.
- El costo cuadrático en K hace lentos los casos grandes (K = 128, N = 28 ya supera el millón de pasos).
- `SR` y `SL` están implementados pero el diagrama no los usa.
- Sólo hay una regla implementada (`Regla110`), aunque el diseño admite otras `ReglaLocal`.

---

## 19. Archivos generados

| Archivo | Contenido | Cómo se genera |
|---|---|---|
| [`docs/diagrama_bloques.md`](diagrama_bloques.md) | Los 51 nodos del diagrama | `mvn compile exec:java` |
| [`docs/tabla_transiciones.md`](tabla_transiciones.md) | Las 705 transiciones con su bloque de origen | `mvn compile exec:java` |
| [`docs/resultados_casos.md`](resultados_casos.md) | Resultado de cada caso de prueba | `mvn compile exec:java` |
| [`docs/INFORME.md`](INFORME.md) | Informe técnico | Manual |
| [`docs/DECISIONES.md`](DECISIONES.md) | Decisiones de diseño | Manual |
| `turing-app.log` | Registro de la aplicación | Al ejecutar |

---

## 20. Referencias

- Turing, A. M. (1936). *On Computable Numbers, with an Application to the Entscheidungsproblem*. Proceedings of
  the London Mathematical Society, 2(42), 230–265.
- Wolfram, S. (1983). *Statistical Mechanics of Cellular Automata*. Reviews of Modern Physics, 55(3), 601–644.
- Wolfram, S. (2002). *A New Kind of Science*. Wolfram Media.
- Cook, M. (2004). *Universality in Elementary Cellular Automata*. Complex Systems, 15(1), 1–40.
- Lewis, H. R., & Papadimitriou, C. H. (1998). *Elements of the Theory of Computation* (2.ª ed.). Prentice Hall.
- Hopcroft, J. E., Motwani, R., & Ullman, J. D. (2007). *Introducción a la teoría de autómatas, lenguajes y
  computación* (3.ª ed.). Pearson.
- Material de cátedra: *Gramáticas Sensibles al Contexto y Máquinas de Turing* (unidad 8).
- Fowler, M. (2018). *Refactoring: Improving the Design of Existing Code* (2.ª ed.). Addison-Wesley.
