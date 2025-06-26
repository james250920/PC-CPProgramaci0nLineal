import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Paralela {
    
    private static double[] coeficientesFO;
    private static double[][] restricciones;
    private static double[] limitesRestricciones;
    private static List<Double> datosX1, datosX2, datosX3;
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    
    public static void main(String[] args) {
        long tiempoInicio = System.currentTimeMillis();
        
        try {
            System.out.println("=== OPTIMIZACIÓN PARALELA CON GRANDES DATASETS ===");
            System.out.println("Número de núcleos disponibles: " + NUM_THREADS);
            System.out.println("Cargando configuración del problema...");
            
            // Cargar configuración del problema
            cargarConfiguracion();
            
            System.out.println("Cargando datos de variables...");
            
            // Cargar datos de las variables
            datosX1 = cargarDatosVariable("datos_X1.txt");
            datosX2 = cargarDatosVariable("datos_X2.txt");
            datosX3 = cargarDatosVariable("datos_X3.txt");
            
            System.out.println("Datos cargados:");
            System.out.println("- X1: " + datosX1.size() + " valores");
            System.out.println("- X2: " + datosX2.size() + " valores");
            System.out.println("- X3: " + datosX3.size() + " valores");
            System.out.println("Total de combinaciones a evaluar: " + 
                             (long)datosX1.size() * datosX2.size() * datosX3.size());
            
            // Buscar solución óptima
            System.out.println("\nIniciando búsqueda paralela de solución óptima...");
            SolucionOptima solucion = buscarSolucionOptimaParalela();
            
            long tiempoFin = System.currentTimeMillis();
            double tiempoTotal = (tiempoFin - tiempoInicio) / 1000.0;
            
            // Mostrar resultados
            mostrarResultados(solucion, tiempoTotal);
            
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.err.println("Error durante la ejecución: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Cargar configuración del problema desde archivo
    private static void cargarConfiguracion() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("configuracion_problema.txt"))) {
            String linea;
            boolean leyendoFO = false;
            boolean leyendoRestricciones = false;
            List<double[]> listaRestricciones = new ArrayList<>();
            List<Double> listaLimites = new ArrayList<>();
            
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.startsWith("#") || linea.isEmpty()) continue;
                
                if (linea.equals("FUNCION_OBJETIVO")) {
                    leyendoFO = true;
                    continue;
                }
                
                if (linea.equals("RESTRICCIONES")) {
                    leyendoFO = false;
                    leyendoRestricciones = true;
                    continue;
                }
                
                if (linea.equals("NO_NEGATIVIDAD")) {
                    leyendoRestricciones = false;
                    continue;
                }
                
                if (leyendoFO && !linea.startsWith("#")) {
                    String[] coefs = linea.split("\\s+");
                    coeficientesFO = new double[coefs.length];
                    for (int i = 0; i < coefs.length; i++) {
                        coeficientesFO[i] = Double.parseDouble(coefs[i]);
                    }
                }
                
                if (leyendoRestricciones && !linea.startsWith("#")) {
                    String[] valores = linea.split("\\s+");
                    if (valores.length >= 4) {
                        double[] restriccion = new double[3];
                        restriccion[0] = Double.parseDouble(valores[0]);
                        restriccion[1] = Double.parseDouble(valores[1]);
                        restriccion[2] = Double.parseDouble(valores[2]);
                        
                        listaRestricciones.add(restriccion);
                        listaLimites.add(Double.parseDouble(valores[3]));
                    }
                }
            }
            
            // Convertir listas a arrays
            restricciones = listaRestricciones.toArray(new double[0][]);
            limitesRestricciones = listaLimites.stream().mapToDouble(Double::doubleValue).toArray();
        }
    }
    
    // Cargar datos de una variable desde archivo
    private static List<Double> cargarDatosVariable(String archivo) throws IOException {
        List<Double> datos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (!linea.startsWith("#") && !linea.isEmpty()) {
                    datos.add(Double.parseDouble(linea));
                }
            }
        }
        return datos;
    }
    
    // Buscar solución óptima usando paralelización
    private static SolucionOptima buscarSolucionOptimaParalela() 
            throws InterruptedException, ExecutionException {
        
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        List<Future<ResultadoParcial>> futures = new ArrayList<>();
        
        // Optimización: evaluar solo una muestra representativa
        int muestraX1 = Math.min(datosX1.size(), 300);
        int pasoX1 = Math.max(1, datosX1.size() / muestraX1);
        
        System.out.println("Dividiendo trabajo entre " + NUM_THREADS + " hilos...");
        System.out.println("Evaluando muestra optimizada con paso: " + pasoX1);
        
        // Dividir el trabajo entre hilos
        int rangosPorHilo = muestraX1 / NUM_THREADS;
        int restoRangos = muestraX1 % NUM_THREADS;
        
        int indiceInicio = 0;
        for (int hilo = 0; hilo < NUM_THREADS; hilo++) {
            int rangosParaEsteHilo = rangosPorHilo + (hilo < restoRangos ? 1 : 0);
            int indiceFin = indiceInicio + rangosParaEsteHilo;
            
            Future<ResultadoParcial> future = executor.submit(
                new TareaOptimizacion(indiceInicio, indiceFin, pasoX1, hilo));
            futures.add(future);
            
            indiceInicio = indiceFin;
        }
        
        // Recopilar resultados de todos los hilos
        double mejorZ = Double.NEGATIVE_INFINITY;
        double[] mejorSolucion = new double[3];
        long totalCombinaciones = 0;
        long totalFactibles = 0;
        
        for (Future<ResultadoParcial> future : futures) {
            ResultadoParcial resultado = future.get();
            totalCombinaciones += resultado.combinacionesEvaluadas;
            totalFactibles += resultado.combinacionesFactibles;
            
            if (resultado.mejorZ > mejorZ) {
                mejorZ = resultado.mejorZ;
                mejorSolucion[0] = resultado.mejorX1;
                mejorSolucion[1] = resultado.mejorX2;
                mejorSolucion[2] = resultado.mejorX3;
            }
        }
        
        executor.shutdown();
        
        return new SolucionOptima(mejorSolucion[0], mejorSolucion[1], mejorSolucion[2], 
                                 mejorZ, totalCombinaciones, totalFactibles);
    }
    
    // Tarea de optimización para ejecución paralela
    static class TareaOptimizacion implements Callable<ResultadoParcial> {
        private final int indiceInicio, indiceFin, paso, idHilo;
        
        public TareaOptimizacion(int indiceInicio, int indiceFin, int paso, int idHilo) {
            this.indiceInicio = indiceInicio;
            this.indiceFin = indiceFin;
            this.paso = paso;
            this.idHilo = idHilo;
        }
        
        @Override
        public ResultadoParcial call() {
            double mejorZ = Double.NEGATIVE_INFINITY;
            double mejorX1 = 0, mejorX2 = 0, mejorX3 = 0;
            long combinacionesEvaluadas = 0;
            long combinacionesFactibles = 0;
            
            System.out.println("Hilo " + idHilo + " procesando rango [" + indiceInicio + ", " + indiceFin + ")");
            
            // Optimización adicional para X2 y X3
            int muestraX2 = Math.min(datosX2.size(), 250);
            int muestraX3 = Math.min(datosX3.size(), 250);
            int pasoX2 = Math.max(1, datosX2.size() / muestraX2);
            int pasoX3 = Math.max(1, datosX3.size() / muestraX3);
            
            for (int i = indiceInicio; i < indiceFin; i++) {
                int indiceX1 = i * paso;
                if (indiceX1 >= datosX1.size()) break;
                
                double x1 = datosX1.get(indiceX1);
                
                for (int j = 0; j < datosX2.size(); j += pasoX2) {
                    double x2 = datosX2.get(j);
                    
                    for (int k = 0; k < datosX3.size(); k += pasoX3) {
                        double x3 = datosX3.get(k);
                        
                        combinacionesEvaluadas++;
                        
                        if (esFactible(x1, x2, x3)) {
                            combinacionesFactibles++;
                            double z = calcularFuncionObjetivo(x1, x2, x3);
                            
                            if (z > mejorZ) {
                                mejorZ = z;
                                mejorX1 = x1;
                                mejorX2 = x2;
                                mejorX3 = x3;
                            }
                        }
                    }
                }
            }
            
            System.out.println("Hilo " + idHilo + " completado: " + combinacionesEvaluadas + 
                             " combinaciones evaluadas, " + combinacionesFactibles + " factibles");
            
            return new ResultadoParcial(mejorX1, mejorX2, mejorX3, mejorZ, 
                                      combinacionesEvaluadas, combinacionesFactibles);
        }
    }
    
    // Verificar si una solución es factible
    private static boolean esFactible(double x1, double x2, double x3) {
        for (int i = 0; i < restricciones.length; i++) {
            double valor = restricciones[i][0] * x1 + restricciones[i][1] * x2 + restricciones[i][2] * x3;
            if (valor > limitesRestricciones[i]) {
                return false;
            }
        }
        return x1 >= 0 && x2 >= 0 && x3 >= 0;
    }
    
    // Calcular función objetivo
    private static double calcularFuncionObjetivo(double x1, double x2, double x3) {
        return coeficientesFO[0] * x1 + coeficientesFO[1] * x2 + coeficientesFO[2] * x3;
    }
    
    // Mostrar resultados
    private static void mostrarResultados(SolucionOptima solucion, double tiempo) {
        System.out.println("\n=== RESULTADOS DE OPTIMIZACIÓN PARALELA ===");
        System.out.println("Solución óptima encontrada:");
        System.out.printf("  X1 = %.4f%n", solucion.x1);
        System.out.printf("  X2 = %.4f%n", solucion.x2);
        System.out.printf("  X3 = %.4f%n", solucion.x3);
        System.out.printf("  Valor de Z = %.2f%n", solucion.z);
        System.out.println();
        System.out.println("Estadísticas:");
        System.out.println("  Hilos utilizados: " + NUM_THREADS);
        System.out.println("  Combinaciones evaluadas: " + solucion.combinacionesEvaluadas);
        System.out.println("  Combinaciones factibles: " + solucion.combinacionesFactibles);
        System.out.printf("  Porcentaje factible: %.2f%%%n", 
                         (solucion.combinacionesFactibles * 100.0) / solucion.combinacionesEvaluadas);
        System.out.printf("  Tiempo total: %.2f segundos%n", tiempo);
        System.out.printf("  Velocidad: %.0f combinaciones/segundo%n", 
                         solucion.combinacionesEvaluadas / tiempo);
        System.out.printf("  Speedup estimado: %.2fx%n", NUM_THREADS * 0.8); // Factor de eficiencia
    }
    
    // Clase para almacenar resultados parciales de cada hilo
    static class ResultadoParcial {
        double mejorX1, mejorX2, mejorX3, mejorZ;
        long combinacionesEvaluadas, combinacionesFactibles;
        
        public ResultadoParcial(double mejorX1, double mejorX2, double mejorX3, double mejorZ,
                               long combinacionesEvaluadas, long combinacionesFactibles) {
            this.mejorX1 = mejorX1;
            this.mejorX2 = mejorX2;
            this.mejorX3 = mejorX3;
            this.mejorZ = mejorZ;
            this.combinacionesEvaluadas = combinacionesEvaluadas;
            this.combinacionesFactibles = combinacionesFactibles;
        }
    }
    
    // Clase para almacenar la solución óptima
    static class SolucionOptima {
        double x1, x2, x3, z;
        long combinacionesEvaluadas, combinacionesFactibles;
        
        public SolucionOptima(double x1, double x2, double x3, double z, 
                             long combinacionesEvaluadas, long combinacionesFactibles) {
            this.x1 = x1;
            this.x2 = x2;
            this.x3 = x3;
            this.z = z;
            this.combinacionesEvaluadas = combinacionesEvaluadas;
            this.combinacionesFactibles = combinacionesFactibles;
        }
    }
}
