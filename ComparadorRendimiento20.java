public class ComparadorRendimiento20 {
    
    public static void main(String[] args) {
        System.out.println("=== COMPARADOR DE RENDIMIENTO - 20 VARIABLES ===");
        System.out.println("Generando datos y comparando algoritmos serial vs paralelo");
        System.out.println("para problemas de programación lineal con 20 variables");
        System.out.println("=" .repeat(70));
        
        try {
            // Paso 1: Generar datos
            System.out.println("\n1. GENERANDO DATOS PARA 20 VARIABLES...");
            long tiempoGeneracion = System.currentTimeMillis();
            Generador.main(new String[]{});
            tiempoGeneracion = System.currentTimeMillis() - tiempoGeneracion;
            System.out.printf("Tiempo de generación: %.2f segundos%n", tiempoGeneracion / 1000.0);
            
            // Paso 2: Ejecutar versión serial
            System.out.println("\n" + "=" .repeat(70));
            System.out.println("2. EJECUTANDO ALGORITMO SERIAL (20 VARIABLES)...");
            long tiempoSerial = System.currentTimeMillis();
            Serial20.main(new String[]{});
            tiempoSerial = System.currentTimeMillis() - tiempoSerial;
            
            // Paso 3: Ejecutar versión paralela
            System.out.println("\n" + "=" .repeat(70));
            System.out.println("3. EJECUTANDO ALGORITMO PARALELO (20 VARIABLES)...");
            long tiempoParalelo = System.currentTimeMillis();
            // Asegúrate de que la clase Paralela20 esté definida e importada correctamente.
            Paralela20.main(new String[]{});
            tiempoParalelo = System.currentTimeMillis() - tiempoParalelo;
            
            // Paso 4: Comparar resultados
            System.out.println("\n" + "=" .repeat(70));
            System.out.println("4. COMPARACIÓN DE RENDIMIENTO - 20 VARIABLES");
            System.out.println("=" .repeat(70));
            System.out.printf("Tiempo generación de datos: %.2f segundos%n", tiempoGeneracion / 1000.0);
            System.out.printf("Tiempo algoritmo serial:    %.2f segundos%n", tiempoSerial / 1000.0);
            System.out.printf("Tiempo algoritmo paralelo:  %.2f segundos%n", tiempoParalelo / 1000.0);
            
            if (tiempoSerial > tiempoParalelo) {
                double speedup = (double) tiempoSerial / tiempoParalelo;
                System.out.printf("Speedup logrado:            %.2fx%n", speedup);
                System.out.printf("Mejora en rendimiento:      %.1f%%%n", (speedup - 1) * 100);
                System.out.println("¡La paralelización fue efectiva para 20 variables!");
            } else {
                double slowdown = (double) tiempoParalelo / tiempoSerial;
                System.out.printf("Slowdown:                   %.2fx%n", slowdown);
                System.out.println("El algoritmo serial fue más rápido en este caso.");
                System.out.println("Nota: Con 20 variables, la paralelización puede requerir datasets más grandes para ser efectiva.");
            }
            
            System.out.println("\n" + "=" .repeat(70));
            System.out.println("ANÁLISIS COMPLETADO - PROBLEMA DE 20 VARIABLES");
            System.out.println("Archivos generados:");
            for (int i = 1; i <= 20; i++) {
                System.out.println("- datos_X" + i + ".txt");
            }
            System.out.println("- configuracion_problema.txt");
            
            System.out.println("\nCaracterísticas del problema:");
            System.out.println("- Variables de decisión: 20");
            System.out.println("- Restricciones: 8");
            System.out.println("- Complejidad: Exponencial O(n^20)");
            System.out.println("- Estrategia: Muestreo inteligente y paralelización");
            
        } catch (Exception e) {
            System.err.println("Error durante la ejecución: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
