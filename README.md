# Sistema de Optimización de Programación Lineal
## Comparación de Algoritmos Serial vs Paralelo - Escalable hasta 20 Variables

### Descripción del Proyecto

Este proyecto implementa un sistema completo para resolver problemas de programación lineal utilizando tanto algoritmos seriales como paralelos, diseñado para manejar desde 3 hasta 20 variables con grandes volúmenes de datos.

### Versiones Disponibles

#### Versión Original (3 Variables)
- `Generador.java` - Genera datasets para X1, X2, X3
- `Serial.java` - Algoritmo serial optimizado
- `Paralela.java` - Algoritmo paralelo
- `ComparadorRendimiento.java` - Comparador para 3 variables

#### Versión Escalada (20 Variables) - **¡NUEVO!**
- `Generador.java` (modificado) - Genera datasets para X1 hasta X20
- `Serial20.java` - Algoritmo serial para 20 variables
- `Paralela20.java` - Algoritmo paralelo para 20 variables  
- `ComparadorRendimiento20.java` - Comparador para 20 variables

### Problema de Programación Lineal

#### Versión 3 Variables (Original)
**Función Objetivo:**
```
Maximizar Z = 210·X1 + 240·X2 + 130·X3
```

**Restricciones:**
- Madera: 5·X1 + 2·X2 + 4·X3 ≤ [Valor aleatorio]
- Metal: 3·X1 + 5·X2 + 2·X3 ≤ [Valor aleatorio]
- Mano de Obra: 6·X1 + 8·X2 + 4·X3 ≤ [Valor aleatorio]
- Espacio: 2·X1 + 2·X2 + 1·X3 ≤ [Valor aleatorio]
- No negatividad: X1, X2, X3 ≥ 0

#### Versión 20 Variables (Escalada)
**Función Objetivo:**
```
Maximizar Z = Σ(coef_i · Xi) para i=1 hasta 20
```

**Restricciones:**
- 8 restricciones lineales con coeficientes aleatorios
- Cada restricción: Σ(ai · Xi) ≤ bi para i=1 hasta 20
- No negatividad: X1, X2, ..., X20 ≥ 0

### Cómo Usar el Sistema

#### Para 3 Variables (Versión Original)
```bash
# Compilar archivos originales
javac Generador.java Serial.java Paralela.java ComparadorRendimiento.java

# Ejecutar comparación completa
java ComparadorRendimiento
```

#### Para 20 Variables (Versión Escalada)
```bash
# Compilar archivos escalados
javac Generador.java Serial20.java Paralela20.java ComparadorRendimiento20.java

# Ejecutar comparación completa para 20 variables
java ComparadorRendimiento20
```

#### Ejecución Paso a Paso (20 Variables)
```bash
# 1. Generar datos para 20 variables
java Generador

# 2. Ejecutar algoritmo serial
java Serial20

# 3. Ejecutar algoritmo paralelo
java Paralela20
```

### Características Técnicas

#### Optimizaciones Implementadas:
1. **Muestreo Inteligente Escalable**: 
   - 3 Variables: Muestreo hasta 200 valores por variable
   - 20 Variables: Muestreo adaptativo (50 valores base por variable)
2. **Paralelización Eficiente**: Divide el trabajo equitativamente entre hilos
3. **Búsqueda Recursiva**: Optimizada para manejar múltiples variables
4. **Gestión de Memoria**: Carga datos de forma eficiente
5. **Monitoreo de Progreso**: Reporta avance durante la ejecución

#### Métricas de Rendimiento:
- Combinaciones evaluadas por segundo
- Porcentaje de soluciones factibles
- Speedup logrado con paralelización  
- Eficiencia de los hilos
- Escalabilidad del algoritmo

#### Complejidad Algorítmica:
- **3 Variables**: O(n³) con muestreo
- **20 Variables**: O(n²⁰) con muestreo inteligente
- **Paralelización**: Speedup teórico de hasta 8x (dependiendo de núcleos)

### Ejemplo de Salida (20 Variables)

```
=== RESULTADOS DE OPTIMIZACIÓN PARALELA (20 VARIABLES) ===
Solución óptima encontrada:
  X1 = 12.4523
  X2 = 8.7341
  X3 = 15.9876
  ...
  X20 = 3.2156
  Valor de Z = 45,847.23

Estadísticas:
  Variables: 20
  Hilos utilizados: 8
  Combinaciones evaluadas: 2,500,000
  Combinaciones factibles: 1,247,832
  Porcentaje factible: 49.91%
  Tiempo total: 28.45 segundos
  Velocidad: 87,847 combinaciones/segundo
  Speedup estimado: 6.00x
```

### Configuración Avanzada

#### Para modificar el número de datos generados:
**3 Variables (archivos originales):**
```java
// En Generador.java (línea 6)
private static final int NUM_DATOS = 15000; // Cambiar aquí
```

**20 Variables (versión escalada):**
```java  
// En Generador.java (línea 6)
private static final int NUM_DATOS = 5000; // Reducido para 20 variables
```

#### Para ajustar el tamaño de muestra:
**Serial20.java**: Modificar `muestraBase` (línea 148)
**Paralela20.java**: Modificar `muestraBase` (línea 207)

#### Para cambiar el número de variables:
```java
// En archivos *20.java
private static final int NUM_VARIABLES = 20; // Cambiar aquí
```

### Requisitos del Sistema

#### Mínimos:
- Java 8 o superior
- Memoria RAM: 4GB 
- Procesador: Dual-core
- Espacio en disco: 100MB

#### Recomendados para 20 Variables:
- Java 11 o superior
- Memoria RAM: 8GB o más
- Procesador: Octa-core o superior
- Espacio en disco: 200MB
- SSD para mejor rendimiento de I/O

### Casos de Uso

1. **Investigación Académica**: 
   - Comparar rendimiento serial vs paralelo
   - Estudiar escalabilidad de algoritmos
   - Análisis de complejidad computacional

2. **Optimización Industrial**: 
   - Problemas de asignación de recursos
   - Planificación de producción
   - Distribución de inventarios

3. **Educación**: 
   - Demostrar conceptos de programación paralela
   - Enseñar optimización computacional
   - Mostrar trade-offs de rendimiento

4. **Benchmarking**: 
   - Evaluar rendimiento de hardware
   - Comparar eficiencia de algoritmos
   - Medir speedup de paralelización

### Notas Técnicas

#### Limitaciones y Consideraciones:
- **3 Variables**: Algoritmo completo de fuerza bruta con muestreo
- **20 Variables**: Requiere muestreo agresivo debido a complejidad exponencial
- **Muestreo**: Puede no encontrar la solución óptima global, pero proporciona buenas aproximaciones
- **Paralelización**: Más efectiva con datasets grandes y múltiples núcleos
- **Memoria**: El consumo crece linealmente con el número de variables y datos

#### Archivos Generados:
- **3 Variables**: 4 archivos (datos_X1.txt, datos_X2.txt, datos_X3.txt, configuracion_problema.txt)
- **20 Variables**: 21 archivos (datos_X1.txt hasta datos_X20.txt, configuracion_problema.txt)

### Posibles Mejoras

1. **Algoritmos Avanzados**: 
   - Implementar algoritmo Simplex
   - Agregar método de puntos interiores
   - Incorporar algoritmos genéticos

2. **Interfaz y Visualización**:
   - Interfaz gráfica de usuario
   - Visualización de resultados en tiempo real
   - Gráficos de convergencia

3. **Escalabilidad**:
   - Soporte para 50+ variables
   - Distribución en cluster
   - GPU acceleration

4. **Exportación y Análisis**:
   - Exportación a CSV, JSON, XML
   - Integración con R/Python
   - Reportes automáticos

5. **Optimizaciones**:
   - Cache de resultados
   - Poda de búsqueda
   - Heurísticas inteligentes
