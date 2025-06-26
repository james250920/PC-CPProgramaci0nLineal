import java.io.*;
import java.util.*;

public class Generador {
    
    private static final int NUM_DATOS = 5000; // Número de datos por variable (reducido para 20 variables)
    private static final int NUM_VARIABLES = 20; // Número total de variables
    
    public static void main(String[] args) {
        Random rand = new Random();
        
        System.out.println("Generando " + NUM_DATOS + " datos para cada una de las " + NUM_VARIABLES + " variables...");
        
        try {
            // Generar datos para todas las variables X1 a X20
            for (int i = 1; i <= NUM_VARIABLES; i++) {
                String archivo = "datos_X" + i + ".txt";
                String varName = "X" + i;
                // Rangos variables para mayor diversidad
                double min = 0;
                double max = 50 + (i * 10); // Rangos incrementales
                generarDatosVariable(archivo, varName, NUM_DATOS, rand, min, max);
            }
            
            // Generar archivo de configuración del problema
            generarConfiguracionProblema(rand);
            
            System.out.println("Todos los archivos generados exitosamente:");
            for (int i = 1; i <= NUM_VARIABLES; i++) {
                System.out.println("- datos_X" + i + ".txt (" + NUM_DATOS + " valores)");
            }
            System.out.println("- configuracion_problema.txt");
            
        } catch (IOException e) {
            System.err.println("Error al generar archivos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Generar datos para una variable específica
    private static void generarDatosVariable(String fileName, String varName, 
                                           int numDatos, Random rand, 
                                           double min, double max) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("# Datos para variable " + varName);
            writer.newLine();
            writer.write("# Cantidad de datos: " + numDatos);
            writer.newLine();
            writer.write("# Rango: [" + min + ", " + max + "]");
            writer.newLine();
            writer.newLine();
            
            for (int i = 0; i < numDatos; i++) {
                double valor = min + (max - min) * rand.nextDouble();
                writer.write(String.format("%.4f", valor));
                writer.newLine();
            }
        }
        System.out.println("Archivo " + fileName + " generado con " + numDatos + " datos.");
    }
    
    // Generar configuración del problema de programación lineal con 20 variables
    private static void generarConfiguracionProblema(Random rand) throws IOException {
        // Valores aleatorios para las restricciones (más restricciones para 20 variables)
        int numRestricciones = 8; // Aumentado para manejar 20 variables
        double[] limitesRestricciones = new double[numRestricciones];
        
        for (int i = 0; i < numRestricciones; i++) {
            limitesRestricciones[i] = rand.nextDouble() * 1000 + 200; // Entre 200 y 1200
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("configuracion_problema.txt"))) {
            writer.write("# Configuración del Problema de Programación Lineal con 20 Variables");
            writer.newLine();
            writer.write("# Función Objetivo: Maximizar Z = Σ(coef_i * X_i) para i=1 hasta 20");
            writer.newLine();
            writer.newLine();
            
            writer.write("FUNCION_OBJETIVO");
            writer.newLine();
            // Generar coeficientes aleatorios para la función objetivo
            StringBuilder coeficientes = new StringBuilder();
            for (int i = 1; i <= NUM_VARIABLES; i++) {
                double coef = rand.nextDouble() * 400 + 100; // Entre 100 y 500
                coeficientes.append(String.format("%.1f", coef));
                if (i < NUM_VARIABLES) coeficientes.append(" ");
            }
            writer.write(coeficientes.toString());
            writer.newLine();
            writer.newLine();
            
            writer.write("RESTRICCIONES");
            writer.newLine();
            
            // Generar restricciones aleatorias
            String[] nombresRestricciones = {"Materiales", "Recursos", "Capacidad", "Tiempo", 
                                           "Presupuesto", "Espacio", "Personal", "Calidad"};
            
            for (int r = 0; r < numRestricciones; r++) {
                writer.write("# " + nombresRestricciones[r] + ": ");
                StringBuilder restriccion = new StringBuilder();
                StringBuilder ecuacion = new StringBuilder();
                
                for (int i = 1; i <= NUM_VARIABLES; i++) {
                    double coef = rand.nextDouble() * 10 + 0.5; // Entre 0.5 y 10.5
                    restriccion.append(String.format("%.2f", coef));
                    if (i < NUM_VARIABLES) restriccion.append(" ");
                    
                    // Para el comentario
                    if (i > 1) ecuacion.append(" + ");
                    ecuacion.append(String.format("%.2f*X%d", coef, i));
                }
                
                restriccion.append(" ").append(String.format("%.2f", limitesRestricciones[r]));
                ecuacion.append(" <= ").append(String.format("%.2f", limitesRestricciones[r]));
                
                writer.write(ecuacion.toString());
                writer.newLine();
                writer.write(restriccion.toString());
                writer.newLine();
            }
            
            writer.newLine();
            writer.write("NO_NEGATIVIDAD");
            writer.newLine();
            StringBuilder noNeg = new StringBuilder();
            for (int i = 1; i <= NUM_VARIABLES; i++) {
                noNeg.append("X").append(i).append(" >= 0");
                if (i < NUM_VARIABLES) noNeg.append(", ");
            }
            writer.write(noNeg.toString());
            writer.newLine();
        }
    }
}
