import java.io.*;
import java.util.*;

public class Serial20 {
    
    private static final int NUM_VARIABLES = 20;
    private static double[] coeficientesFO; // Coeficientes función objetivo
    private static double[][] restricciones; // Matriz de restricciones
    private static double[] limitesRestricciones; // Límites de restricciones
    private static List<List<Double>> datosVariables; // Datos de todas las variables
    
    public static void main(String[] args) {
        long tiempoInicio = System.currentTimeMillis();
        
        try {
            System.out.println("=== OPTIMIZACIÓN SERIAL CON 20 VARIABLES ===");
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
            System.out.println("\nIniciando búsqueda de solución óptima...");
            SolucionOptima solucion = buscarSolucionOptima();
            
            long tiempoFin = System.currentTimeMillis();
            double tiempoTotal = (tiempoFin - tiempoInicio) / 1000.0;
            
            // Mostrar resultados
            mostrarResultados(solucion, tiempoTotal);
            
        } catch (IOException e) {
            System.err.println("Error al procesar archivos: " + e.getMessage());
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
    
    // Buscar solución óptima usando algoritmo serial con muestreo inteligente
    private static SolucionOptima buscarSolucionOptima() {
        double mejorZ = Double.NEGATIVE_INFINITY;
        double[] mejorSolucion = new double[NUM_VARIABLES];
        long combinacionesEvaluadas = 0;
        long combinacionesFactibles = 0;
        
        // Muestreo adaptativo para manejar 20 variables
        int[] muestras = new int[NUM_VARIABLES];
        int[] pasos = new int[NUM_VARIABLES];
        
        // Calcular muestras para cada variable
        int muestraBase = 50; // Muestra base por variable
        for (int i = 0; i < NUM_VARIABLES; i++) {
            muestras[i] = Math.min(datosVariables.get(i).size(), muestraBase);
            pasos[i] = Math.max(1, datosVariables.get(i).size() / muestras[i]);
        }
        
        System.out.println("Estrategia de muestreo optimizada:");
        for (int i = 0; i < NUM_VARIABLES; i++) {
            System.out.println("- X" + (i+1) + ": paso " + pasos[i] + " (evaluando " + muestras[i] + " valores)");
        }
        
        // Búsqueda recursiva optimizada
        int[] indices = new int[NUM_VARIABLES];
        double[] solucionActual = new double[NUM_VARIABLES];
        
        ResultadoBusqueda resultado = busquedaRecursiva(0, indices, solucionActual, pasos);
        
        return new SolucionOptima(resultado.mejorSolucion, resultado.mejorZ, 
                                resultado.combinacionesEvaluadas, resultado.combinacionesFactibles);
    }
    
    // Búsqueda recursiva para manejar 20 variables de forma eficiente
    private static ResultadoBusqueda busquedaRecursiva(int nivel, int[] indices, double[] solucionActual, int[] pasos) {
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
            
            ResultadoBusqueda resultadoParcial = busquedaRecursiva(nivel + 1, indices, solucionActual, pasos);
            
            mejorResultado.combinacionesEvaluadas += resultadoParcial.combinacionesEvaluadas;
            mejorResultado.combinacionesFactibles += resultadoParcial.combinacionesFactibles;
            
            if (resultadoParcial.mejorZ > mejorResultado.mejorZ) {
                mejorResultado.mejorZ = resultadoParcial.mejorZ;
                mejorResultado.mejorSolucion = resultadoParcial.mejorSolucion.clone();
            }
            
            // Progreso cada 100,000 combinaciones
            if (mejorResultado.combinacionesEvaluadas % 100000 == 0) {
                System.out.println("Progreso: " + mejorResultado.combinacionesEvaluadas + 
                                 " combinaciones evaluadas, mejor Z: " + 
                                 String.format("%.2f", mejorResultado.mejorZ));
            }
        }
        
        return mejorResultado;
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
        System.out.println("\n=== RESULTADOS DE OPTIMIZACIÓN SERIAL (20 VARIABLES) ===");
        System.out.println("Solución óptima encontrada:");
        for (int i = 0; i < NUM_VARIABLES; i++) {
            System.out.printf("  X%d = %.4f%n", i+1, solucion.variables[i]);
        }
        System.out.printf("  Valor de Z = %.2f%n", solucion.z);
        System.out.println();
        System.out.println("Estadísticas:");
        System.out.println("  Variables: " + NUM_VARIABLES);
        System.out.println("  Combinaciones evaluadas: " + solucion.combinacionesEvaluadas);
        System.out.println("  Combinaciones factibles: " + solucion.combinacionesFactibles);
        System.out.printf("  Porcentaje factible: %.4f%%%n", 
                         (solucion.combinacionesFactibles * 100.0) / solucion.combinacionesEvaluadas);
        System.out.printf("  Tiempo total: %.2f segundos%n", tiempo);
        System.out.printf("  Velocidad: %.0f combinaciones/segundo%n", 
                         solucion.combinacionesEvaluadas / tiempo);
    }
    
    // Clase auxiliar para la búsqueda recursiva
    static class ResultadoBusqueda {
        double[] mejorSolucion = new double[NUM_VARIABLES];
        double mejorZ = Double.NEGATIVE_INFINITY;
        long combinacionesEvaluadas = 0;
        long combinacionesFactibles = 0;
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
