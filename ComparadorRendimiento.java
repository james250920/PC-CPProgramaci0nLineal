public class ComparadorRendimiento {
    
    public static void main(String[] args) {
        System.out.println("=== COMPARADOR DE RENDIMIENTO ===");
        System.out.println("Generando datos y comparando algoritmos serial vs paralelo");
        System.out.println("para problemas de programación lineal con grandes datasets");
        System.out.println("=" .repeat(60));
        
        try {
            // Paso 1: Generar datos
            System.out.println("\n1. GENERANDO DATOS...");
            long tiempoGeneracion = System.currentTimeMillis();
            Generador.main(new String[]{});
            tiempoGeneracion = System.currentTimeMillis() - tiempoGeneracion;
            System.out.printf("Tiempo de generación: %.2f segundos%n", tiempoGeneracion / 1000.0);
            
            // Paso 2: Ejecutar versión serial
            System.out.println("\n" + "=" .repeat(60));
            System.out.println("2. EJECUTANDO ALGORITMO SERIAL...");
            long tiempoSerial = System.currentTimeMillis();
            Serial.main(new String[]{});
            tiempoSerial = System.currentTimeMillis() - tiempoSerial;
            
            // Paso 3: Ejecutar versión paralela
            System.out.println("\n" + "=" .repeat(60));
            System.out.println("3. EJECUTANDO ALGORITMO PARALELO...");
            long tiempoParalelo = System.currentTimeMillis();
            Paralela.main(new String[]{});
            tiempoParalelo = System.currentTimeMillis() - tiempoParalelo;
            
            // Paso 4: Comparar resultados
            System.out.println("\n" + "=" .repeat(60));
            System.out.println("4. COMPARACIÓN DE RENDIMIENTO");
            System.out.println("=" .repeat(60));
            System.out.printf("Tiempo generación de datos: %.2f segundos%n", tiempoGeneracion / 1000.0);
            System.out.printf("Tiempo algoritmo serial:    %.2f segundos%n", tiempoSerial / 1000.0);
            System.out.printf("Tiempo algoritmo paralelo:  %.2f segundos%n", tiempoParalelo / 1000.0);
            
            if (tiempoSerial > tiempoParalelo) {
                double speedup = (double) tiempoSerial / tiempoParalelo;
                System.out.printf("Speedup logrado:            %.2fx%n", speedup);
                System.out.printf("Mejora en rendimiento:      %.1f%%%n", (speedup - 1) * 100);
            } else {
                System.out.println("El algoritmo serial fue más rápido en este caso.");
            }
            
            System.out.println("\n" + "=" .repeat(60));
            System.out.println("ANÁLISIS COMPLETADO");
            System.out.println("Archivos generados:");
            System.out.println("- datos_X1.txt");
            System.out.println("- datos_X2.txt");
            System.out.println("- datos_X3.txt");
            System.out.println("- configuracion_problema.txt");
            
        } catch (Exception e) {
            System.err.println("Error durante la ejecución: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
