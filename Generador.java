import java.io.*;
import java.util.*;

public class Generador {
    
    private static final int NUM_DATOS = 15000; // Número de datos por variable
    
    public static void main(String[] args) {
        Random rand = new Random();
        
        System.out.println("Generando " + NUM_DATOS + " datos para cada variable...");
        
        try {
            // Generar datos para X1 (Mesas)
            generarDatosVariable("datos_X1.txt", "X1", NUM_DATOS, rand, 0, 150);
            
            // Generar datos para X2 (Sillas)
            generarDatosVariable("datos_X2.txt", "X2", NUM_DATOS, rand, 0, 120);
            
            // Generar datos para X3 (Estantes)
            generarDatosVariable("datos_X3.txt", "X3", NUM_DATOS, rand, 0, 100);
            
            // Generar archivo de configuración del problema
            generarConfiguracionProblema(rand);
            
            System.out.println("Todos los archivos generados exitosamente:");
            System.out.println("- datos_X1.txt (" + NUM_DATOS + " valores)");
            System.out.println("- datos_X2.txt (" + NUM_DATOS + " valores)");
            System.out.println("- datos_X3.txt (" + NUM_DATOS + " valores)");
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
    
    // Generar configuración del problema de programación lineal
    private static void generarConfiguracionProblema(Random rand) throws IOException {
        // Valores aleatorios para las restricciones
        double madera = rand.nextInt(500) + 100;    // entre 100 y 600
        double metal = rand.nextInt(400) + 100;     // entre 100 y 500
        double manoObra = rand.nextInt(700) + 100;  // entre 100 y 800
        double espacio = rand.nextInt(300) + 50;    // entre 50 y 350
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("configuracion_problema.txt"))) {
            writer.write("# Configuración del Problema de Programación Lineal");
            writer.newLine();
            writer.write("# Función Objetivo: Maximizar Z = 210*X1 + 240*X2 + 130*X3");
            writer.newLine();
            writer.newLine();
            
            writer.write("FUNCION_OBJETIVO");
            writer.newLine();
            writer.write("210.0 240.0 130.0");
            writer.newLine();
            writer.newLine();
            
            writer.write("RESTRICCIONES");
            writer.newLine();
            writer.write("# Madera: 5*X1 + 2*X2 + 4*X3 <= " + madera);
            writer.newLine();
            writer.write("5.0 2.0 4.0 " + madera);
            writer.newLine();
            
            writer.write("# Metal: 3*X1 + 5*X2 + 2*X3 <= " + metal);
            writer.newLine();
            writer.write("3.0 5.0 2.0 " + metal);
            writer.newLine();
            
            writer.write("# Mano de Obra: 6*X1 + 8*X2 + 4*X3 <= " + manoObra);
            writer.newLine();
            writer.write("6.0 8.0 4.0 " + manoObra);
            writer.newLine();
            
            writer.write("# Espacio: 2*X1 + 2*X2 + 1*X3 <= " + espacio);
            writer.newLine();
            writer.write("2.0 2.0 1.0 " + espacio);
            writer.newLine();
            writer.newLine();
            
            writer.write("NO_NEGATIVIDAD");
            writer.newLine();
            writer.write("X1 >= 0, X2 >= 0, X3 >= 0");
            writer.newLine();
        }
    }
}
