import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Paralela20 {
    
    private static final int NUM_VARIABLES = 20;
    private static double[] coeficientesFO;
    private static double[][] restricciones;
    private static double[] limitesRestricciones;
    private static List<List<Double>> datosVariables;
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    
    public static void main(String[] args) {
        long tiempoInicio = System.currentTimeMillis();

        try {
            System.out.println("=== OPTIMIZACIÓN PARALELA CON 20 VARIABLES ===");
            System.out.println("Número de núcleos disponibles: " + NUM_THREADS);
            System.out.println("Cargando configuración del problema...");
            
            // Cargar configuración del problema
            cargarConfiguracion();
            
            System.out.println("Cargando datos de variables...");
            
            // Cargar datos de todas las variables
            datosVariables = new ArrayList<>();
            for (int i = 1; i <= NUM_VARIABLES; i++) {
                List<Double> datos = cargarDatosVariable("datos_X" + i + ".txt");
                datosVariables.add(datos);
                System.out.println("- X" + i + ": " + datos.size() + " valores");
            }
            
            // Calcular total de combinaciones (solo para información)
            long totalCombinaciones = 1;
            for (List<Double> datos : datosVariables) {
                totalCombinaciones *= datos.size();
                if (totalCombinaciones < 0) { // Overflow
                    totalCombinaciones = Long.MAX_VALUE;
                    break;
                }
            }
            System.out.println("Total de combinaciones teóricas: " + 
                             (totalCombinaciones == Long.MAX_VALUE ? "Más de " + Long.MAX_VALUE : totalCombinaciones));
            
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
                    if (valores.length >= NUM_VARIABLES + 1) {
                        double[] restriccion = new double[NUM_VARIABLES];
                        for (int i = 0; i < NUM_VARIABLES; i++) {
                            restriccion[i] = Double.parseDouble(valores[i]);
                        }
                        
                        listaRestricciones.add(restriccion);
                        listaLimites.add(Double.parseDouble(valores[NUM_VARIABLES]));
                    }
                }
            }
            
            // Convertir listas a arrays
            restricciones = listaRestricciones.toArray(new double[0][]);
            limitesRestricciones = listaLimites.stream().mapToDouble(Double::doubleValue).toArray();
            
            System.out.println("Configuración cargada:");
            System.out.println("- Variables: " + NUM_VARIABLES);
            System.out.println("- Restricciones: " + restricciones.length);
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
        
        // Estrategia de paralelización: dividir el espacio de la primera variable
        int muestraX1 = Math.min(datosVariables.get(0).size(), 200);
        int pasoX1 = Math.max(1, datosVariables.get(0).size() / muestraX1);
        
        System.out.println("Dividiendo trabajo entre " + NUM_THREADS + " hilos...");
        System.out.println("Evaluando muestra optimizada con paso X1: " + pasoX1);
        
        // Dividir el trabajo entre hilos
        int rangosPorHilo = datosVariables.get(0).size() / NUM_THREADS;
        int restoRangos = datosVariables.get(0).size() % NUM_THREADS;

        int indiceInicio = 0;
        // Convertir indiceInicio en una variable final para cumplir con las restricciones de las expresiones lambda
        final int inicio = indiceInicio;
        for (int hilo = 0; hilo < NUM_THREADS; hilo++) {
            int rangosParaEsteHilo = rangosPorHilo + (hilo < restoRangos ? 1 : 0);
            int indiceFin = inicio + rangosParaEsteHilo;

            Future<ResultadoParcial> future = executor.submit(() -> {
                double mejorZ = Double.NEGATIVE_INFINITY;
                double[] mejorSolucion = new double[NUM_VARIABLES];
                long combinacionesEvaluadas = 0;
                long combinacionesFactibles = 0;

                for (int i = inicio; i < indiceFin; i++) {
                    double[] solucionActual = new double[NUM_VARIABLES];
                    solucionActual[0] = datosVariables.get(0).get(i);

                    // Evaluar combinaciones para las demás variables
                    for (int j = 1; j < NUM_VARIABLES; j++) {
                        for (double valor : datosVariables.get(j)) {
                            solucionActual[j] = valor;

                            combinacionesEvaluadas++;
                            if (esFactible(solucionActual)) {
                                combinacionesFactibles++;
                                double z = calcularFuncionObjetivo(solucionActual);
                                if (z > mejorZ) {
                                    mejorZ = z;
                                    mejorSolucion = solucionActual.clone();
                                }
                            }
                        }
                    }
                }

                return new ResultadoParcial(mejorSolucion, mejorZ, combinacionesEvaluadas, combinacionesFactibles);
            });

            futures.add(future);
            indiceInicio = indiceFin;
        }
        
        // Recopilar resultados de todos los hilos
        double mejorZ = Double.NEGATIVE_INFINITY;
        double[] mejorSolucion = new double[NUM_VARIABLES];
        long totalCombinaciones = 0;
        long totalFactibles = 0;
        
        for (Future<ResultadoParcial> future : futures) {
            ResultadoParcial resultado = future.get();
            totalCombinaciones += resultado.combinacionesEvaluadas;
            totalFactibles += resultado.combinacionesFactibles;
            
            if (resultado.mejorZ > mejorZ) {
                mejorZ = resultado.mejorZ;
                mejorSolucion = resultado.mejorSolucion.clone();
            }
        }
        
        executor.shutdown();
        
        return new SolucionOptima(mejorSolucion, mejorZ, totalCombinaciones, totalFactibles);
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
            double[] mejorSolucion = new double[NUM_VARIABLES];
            long combinacionesEvaluadas = 0;
            long combinacionesFactibles = 0;
            
            System.out.println("Hilo " + idHilo + " procesando rango [" + indiceInicio + ", " + indiceFin + ")");
            
            // Muestreo para las demás variables
            int[] muestras = new int[NUM_VARIABLES];
            int[] pasos = new int[NUM_VARIABLES];
            
            // La primera variable ya está definida por el rango del hilo
            muestras[0] = indiceFin - indiceInicio;
            pasos[0] = paso;
            
            // Para las demás variables, usar muestreo más pequeño
            int muestraBase = 15; // Reducido para permitir exploración factible
            for (int i = 1; i < NUM_VARIABLES; i++) {
                muestras[i] = Math.min(datosVariables.get(i).size(), muestraBase);
                pasos[i] = Math.max(1, datosVariables.get(i).size() / muestras[i]);
            }
            
            // Búsqueda recursiva en el hilo
            double[] solucionActual = new double[NUM_VARIABLES];
            
            for (int i = indiceInicio; i < indiceFin; i++) {
                int indiceX1 = i * paso;
                if (indiceX1 >= datosVariables.get(0).size()) break;
                
                solucionActual[0] = datosVariables.get(0).get(indiceX1);
                
                ResultadoBusqueda resultado = busquedaRecursivaHilo(1, solucionActual, pasos);
                
                combinacionesEvaluadas += resultado.combinacionesEvaluadas;
                combinacionesFactibles += resultado.combinacionesFactibles;
                
                if (resultado.mejorZ > mejorZ) {
                    mejorZ = resultado.mejorZ;
                    mejorSolucion = resultado.mejorSolucion.clone();
                }
            }
            
            System.out.println("Hilo " + idHilo + " completado: " + combinacionesEvaluadas + 
                             " combinaciones evaluadas, " + combinacionesFactibles + " factibles, mejor Z: " + 
                             String.format("%.2f", mejorZ));
            
            return new ResultadoParcial(mejorSolucion, mejorZ, combinacionesEvaluadas, combinacionesFactibles);
        }
        
        // Búsqueda recursiva optimizada para hilos
        private ResultadoBusqueda busquedaRecursivaHilo(int nivel, double[] solucionActual, int[] pasos) {
            if (nivel == NUM_VARIABLES) {
                // Evaluamos la solución completa
                ResultadoBusqueda resultado = new ResultadoBusqueda();
                resultado.combinacionesEvaluadas = 1;
                
                if (esFactible(solucionActual)) {
                    resultado.combinacionesFactibles = 1;
                    resultado.mejorZ = calcularFuncionObjetivo(solucionActual);
                    resultado.mejorSolucion = solucionActual.clone();
                } else {
                    resultado.mejorZ = Double.NEGATIVE_INFINITY;
                }
                
                return resultado;
            }
            
            // Recursión para el nivel actual
            ResultadoBusqueda mejorResultado = new ResultadoBusqueda();
            mejorResultado.mejorZ = Double.NEGATIVE_INFINITY;
            mejorResultado.mejorSolucion = new double[NUM_VARIABLES];
            
            for (int i = 0; i < datosVariables.get(nivel).size(); i += pasos[nivel]) {
                solucionActual[nivel] = datosVariables.get(nivel).get(i);
                
                ResultadoBusqueda resultadoParcial = busquedaRecursivaHilo(nivel + 1, solucionActual, pasos);
                
                mejorResultado.combinacionesEvaluadas += resultadoParcial.combinacionesEvaluadas;
                mejorResultado.combinacionesFactibles += resultadoParcial.combinacionesFactibles;
                
                if (resultadoParcial.mejorZ > mejorResultado.mejorZ) {
                    mejorResultado.mejorZ = resultadoParcial.mejorZ;
                    mejorResultado.mejorSolucion = resultadoParcial.mejorSolucion.clone();
                }
            }
            
            return mejorResultado;
        }
    }
    
    // Verificar si una solución es factible
    private static boolean esFactible(double[] solucion) {
        // Verificar no negatividad
        for (double valor : solucion) {
            if (valor < 0) return false;
        }
        
        // Verificar restricciones
        for (int i = 0; i < restricciones.length; i++) {
            double suma = 0;
            for (int j = 0; j < NUM_VARIABLES; j++) {
                suma += restricciones[i][j] * solucion[j];
            }
            if (suma > limitesRestricciones[i]) {
                return false;
            }
        }
        return true;
    }
    
    // Calcular función objetivo
    private static double calcularFuncionObjetivo(double[] solucion) {
        double suma = 0;
        for (int i = 0; i < NUM_VARIABLES; i++) {
            suma += coeficientesFO[i] * solucion[i];
        }
        return suma;
    }
    
    // Mostrar resultados
    private static void mostrarResultados(SolucionOptima solucion, double tiempo) {
        System.out.println("\n=== RESULTADOS DE OPTIMIZACIÓN PARALELA (20 VARIABLES) ===");
        System.out.println("Solución óptima encontrada:");
        for (int i = 0; i < NUM_VARIABLES; i++) {
            System.out.printf("  X%d = %.4f%n", i+1, solucion.variables[i]);
        }
        System.out.printf("  Valor de Z = %.2f%n", solucion.z);
        System.out.println();
        System.out.println("Estadísticas:");
        System.out.println("  Variables: " + NUM_VARIABLES);
        System.out.println("  Hilos utilizados: " + NUM_THREADS);
        System.out.println("  Combinaciones evaluadas: " + solucion.combinacionesEvaluadas);
        System.out.println("  Combinaciones factibles: " + solucion.combinacionesFactibles);
        System.out.printf("  Porcentaje factible: %.4f%%%n", 
                         (solucion.combinacionesFactibles * 100.0) / solucion.combinacionesEvaluadas);
        System.out.printf("  Tiempo total: %.2f segundos%n", tiempo);
        System.out.printf("  Velocidad: %.0f combinaciones/segundo%n", 
                         solucion.combinacionesEvaluadas / tiempo);
        System.out.printf("  Speedup estimado: %.2fx%n", NUM_THREADS * 0.75); // Factor de eficiencia para 20 variables
    }
    
    // Clase auxiliar para la búsqueda recursiva
    static class ResultadoBusqueda {
        double[] mejorSolucion = new double[NUM_VARIABLES];
        double mejorZ = Double.NEGATIVE_INFINITY;
        long combinacionesEvaluadas = 0;
        long combinacionesFactibles = 0;
    }
    
    // Clase para almacenar resultados parciales de cada hilo
    static class ResultadoParcial {
        double[] mejorSolucion;
        double mejorZ;
        long combinacionesEvaluadas, combinacionesFactibles;
        
        public ResultadoParcial(double[] mejorSolucion, double mejorZ,
                               long combinacionesEvaluadas, long combinacionesFactibles) {
            this.mejorSolucion = mejorSolucion.clone();
            this.mejorZ = mejorZ;
            this.combinacionesEvaluadas = combinacionesEvaluadas;
            this.combinacionesFactibles = combinacionesFactibles;
        }
    }
    
    // Clase para almacenar la solución óptima
    static class SolucionOptima {
        double[] variables;
        double z;
        long combinacionesEvaluadas, combinacionesFactibles;
        
        public SolucionOptima(double[] variables, double z, 
                             long combinacionesEvaluadas, long combinacionesFactibles) {
            this.variables = variables.clone();
            this.z = z;
            this.combinacionesEvaluadas = combinacionesEvaluadas;
            this.combinacionesFactibles = combinacionesFactibles;
        }
    }
}
