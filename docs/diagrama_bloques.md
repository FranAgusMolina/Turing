# Diagrama de bloques – MT autómata celular – Regla 110

Nodo inicial: `INICIO`

| Nodo | Bloque | Flechas (símbolo → destino) | Función |
|---|---|---|---|
| `INICIO` | `R#` | `*` → `DEC` | Desde el contador K avanza hasta el primer # |
| `DEC` | `L` | `0` → `DEC_1`<br>`1` → `DEC_0`<br>`Δ` → `CERO` | Se para sobre el dígito menos significativo pendiente de K |
| `DEC_1` | `1` | `*` → `DEC` | Borrow: 0 → 1 y sigue a la izquierda |
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
| `CENTRO_L1C0` | `L{o,i}` | `*` → `DER_L1C0` | Vuelve al centro: la celda marcada más a la derecha |
| `DER_L1C0` | `R` | `0` → `ESC_FIN_0`<br>`1` → `ESC_FIN_1`<br>`#` → `CICLO_DER_L1C0` | Mira la celda de la derecha |
| `CICLO_DER_L1C0` | `L#` | `*` → `CICLO_DER_R_L1C0` | Frontera cíclica: va al # de apertura de Gt |
| `CICLO_DER_R_L1C0` | `R` | `o` → `ESC_FIN_0`<br>`i` → `ESC_FIN_1` | Lee la primera celda de Gt como vecino derecho |
| `MARCAR_C1` | `i` | `*` → `IZQ_C1` | Marca la celda central c=1 |
| `IZQ_C1` | `L` | `o` → `IR_CIERRE_L0C1`<br>`i` → `IR_CIERRE_L1C1`<br>`#` → `CICLO_IZQ_C1` | Mira la celda de la izquierda (c=1) |
| `CICLO_IZQ_C1` | `R#` | `*` → `CICLO_IZQ_L_C1` | Frontera cíclica: va al # de cierre de Gt (c=1) |
| `CICLO_IZQ_L_C1` | `L` | `0,o` → `IR_CIERRE_L0C1`<br>`1,i` → `IR_CIERRE_L1C1` | Lee la última celda de Gt como vecino izquierdo (c=1) |
| `IR_CIERRE_L0C1` | `R#` | `*` → `CENTRO_L0C1` | Conoce l=0, c=1: va al # de cierre de Gt |
| `CENTRO_L0C1` | `L{o,i}` | `*` → `DER_L0C1` | Vuelve al centro: la celda marcada más a la derecha |
| `DER_L0C1` | `R` | `0` → `ESC_FIN_1`<br>`1` → `ESC_FIN_1`<br>`#` → `CICLO_DER_L0C1` | Mira la celda de la derecha |
| `CICLO_DER_L0C1` | `L#` | `*` → `CICLO_DER_R_L0C1` | Frontera cíclica: va al # de apertura de Gt |
| `CICLO_DER_R_L0C1` | `R` | `o` → `ESC_FIN_1`<br>`i` → `ESC_FIN_1` | Lee la primera celda de Gt como vecino derecho |
| `IR_CIERRE_L1C1` | `R#` | `*` → `CENTRO_L1C1` | Conoce l=1, c=1: va al # de cierre de Gt |
| `CENTRO_L1C1` | `L{o,i}` | `*` → `DER_L1C1` | Vuelve al centro: la celda marcada más a la derecha |
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
