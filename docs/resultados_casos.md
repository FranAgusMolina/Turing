# Resultados de los casos de prueba – Regla 110

| Línea | Entrada | Generación final (MT) | Pasos | Resultado | Descripción |
|---|---|---|---|---|---|
| 6 | `100#000000010000000#` | `000111110000000` | 6341 | ✔ correcto | Ejemplo 1 del enunciado (K=4, N=15) |
| 7 | `1110#01101001#` | `01101110` | 10317 | ✔ correcto | Ejemplo 2 del enunciado (K=14, N=8) |
| 8 | `0#10110010#` | `10110010` | 29 | ✔ correcto | K=0: no se calcula ninguna generación, la cinta queda igual |
| 9 | `1#11000001#` | `01000011` | 635 | ✔ correcto | Frontera cíclica izquierda: la celda 0 ve el vecindario 111 usando la última celda |
| 10 | `1#10000010#` | `10000111` | 635 | ✔ correcto | Frontera cíclica derecha: la última celda ve el vecindario 101 usando la primera |
| 11 | `101#00000000#` | `00000000` | 3292 | ✔ correcto | Todas muertas: la configuración nula es estable (000 → 0) |
| 12 | `101#11111111#` | `00000000` | 3292 | ✔ correcto | Todas vivas: 111 → 0, a la generación siguiente mueren todas |
| 13 | `10#01010101#` | `00000000` | 1277 | ✔ correcto | Patrón alternado |
| 14 | `0011#00100100#` | `10000101` | 1921 | ✔ correcto | K con ceros a la izquierda (K=3) |
| 15 | `1111#0000000000000001#` | `1101011001111101` | 28949 | ✔ correcto | Una sola celda viva en el borde derecho: crecimiento del triángulo hacia la izquierda |
| 16 | `11111#000000000000000000000000000001#` | `110110010011011100111000001101` | 177661 | ✔ correcto | Triángulo de la Regla 110 con N=30 y K=31 (la frontera cíclica entra en juego) |
| 17 | `1100100#10011101011000101101#` | `11011111000111000100` | 450280 | ✔ correcto | K=100 generaciones sobre N=20 |
| 18 | `10000000#0001001101111100010011011111#` | `0111110001001101111100010011` | 1017451 | ✔ correcto | K=128 sobre N=28: control de iteraciones con contador de 8 bits |
| 19 | `1#0101#` | `` | 0 | ✔ rechazada: La hilera debe tener longitud N ≥ 8 (tiene 4). | Entrada inválida: N < 8 |
| 20 | `12#00000000#` | `` | 0 | ✔ rechazada: Formato inválido. Se espera K#celdas# con K binario y celdas 0/1, por ejemplo 100#000000010000000# | Entrada inválida: K no es binario |
| 21 | `101#0000000#0` | `` | 0 | ✔ rechazada: Formato inválido. Se espera K#celdas# con K binario y celdas 0/1, por ejemplo 100#000000010000000# | Entrada inválida: formato incorrecto |
