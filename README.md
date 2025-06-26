# Sistema de Optimización de Programación Lineal
## Comparación de Algoritmos Serial vs Paralelo con Grandes Datasets

### Descripción del Proyecto

Este proyecto implementa un sistema completo para resolver problemas de programación lineal utilizando tanto algoritmos seriales como paralelos, diseñado para manejar grandes volúmenes de datos (10,000+ puntos por variable).

### Archivos del Sistema

#### 1. **Generador.java**
- Genera datasets aleatorios para cada variable (X1, X2, X3)
- Crea 15,000 valores por variable por defecto
- Genera configuración del problema de programación lineal
- **Salida**: 
  - `datos_X1.txt` - Valores para variable X1 (Mesas)
  - `datos_X2.txt` - Valores para variable X2 (Sillas)  
  - `datos_X3.txt` - Valores para variable X3 (Estantes)
  - `configuracion_problema.txt` - Configuración del problema

#### 2. **SerialOptimizado.java**
- Implementación serial optimizada del algoritmo de fuerza bruta
- Carga y procesa grandes datasets
- Utiliza muestreo inteligente para reducir complejidad
- Proporciona estadísticas detalladas de rendimiento

#### 3. **ParalelaOptimizada.java**
- Implementación paralela utilizando ExecutorService
- Divide el trabajo entre múltiples hilos
- Detecta automáticamente el número de núcleos disponibles
- Combina resultados de todos los hilos

#### 4. **ComparadorRendimiento.java**
- Ejecuta ambos algoritmos y compara resultados
- Mide tiempos de ejecución
- Calcula speedup y mejoras de rendimiento
- Genera reporte comparativo completo

### Problema de Programación Lineal

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

### Cómo Usar el Sistema

#### Opción 1: Ejecución Completa (Recomendada)
```bash
# Compilar todos los archivos
javac *.java

# Ejecutar comparación completa
java ComparadorRendimiento
```

#### Opción 2: Ejecución Paso a Paso
```bash
# 1. Generar datos
java Generador

# 2. Ejecutar algoritmo serial
java SerialOptimizado

# 3. Ejecutar algoritmo paralelo
java ParalelaOptimizada
```

### Características Técnicas

#### Optimizaciones Implementadas:
1. **Muestreo Inteligente**: Reduce el espacio de búsqueda manteniendo representatividad
2. **Paralelización Eficiente**: Divide el trabajo equitativamente entre hilos
3. **Gestión de Memoria**: Carga datos de forma eficiente
4. **Monitoreo de Progreso**: Reporta avance durante la ejecución

#### Métricas de Rendimiento:
- Combinaciones evaluadas por segundo
- Porcentaje de soluciones factibles
- Speedup logrado con paralelización  
- Eficiencia de los hilos

### Ejemplo de Salida

```
=== RESULTADOS DE OPTIMIZACIÓN PARALELA ===
Solución óptima encontrada:
  X1 = 45.2341
  X2 = 67.8912
  X3 = 23.1456
  Valor de Z = 25847.23

Estadísticas:
  Hilos utilizados: 8
  Combinaciones evaluadas: 1,250,000
  Combinaciones factibles: 847,532
  Porcentaje factible: 67.81%
  Tiempo total: 12.34 segundos
  Velocidad: 101,294 combinaciones/segundo
  Speedup estimado: 6.40x
```

### Configuración Avanzada

Para modificar el número de datos generados, editar en `Generador.java`:
```java
private static final int NUM_DATOS = 15000; // Cambiar aquí
```

Para ajustar el tamaño de muestra en los algoritmos:
- `SerialOptimizado.java`: Modificar variables `muestraX1`, `muestraX2`, `muestraX3`
- `ParalelaOptimizada.java`: Modificar las mismas variables

### Requisitos del Sistema

- Java 8 o superior
- Memoria RAM: Mínimo 2GB (recomendado 4GB para datasets grandes)
- Procesador: Multinúcleo para aprovechar paralelización
- Espacio en disco: ~50MB para archivos de datos

### Casos de Uso

1. **Investigación Académica**: Comparar rendimiento de algoritmos
2. **Optimización Industrial**: Resolver problemas de asignación de recursos
3. **Análisis de Escalabilidad**: Estudiar comportamiento con grandes volúmenes de datos
4. **Educación**: Demostrar conceptos de programación paralela

### Notas Técnicas

- Los algoritmos utilizan fuerza bruta optimizada, no métodos algebraicos como Simplex
- El muestreo puede no encontrar la solución óptima global, pero proporciona buenas aproximaciones
- La paralelización es más efectiva con datasets grandes y múltiples núcleos
- Los archivos de datos se generan en formato texto plano para facilitar análisis

### Posibles Mejoras

1. Implementar algoritmo Simplex para comparación
2. Agregar visualización gráfica de resultados
3. Soporte para problemas con más variables
4. Interfaz gráfica de usuario
5. Exportación de resultados a formatos estándar (CSV, JSON)
