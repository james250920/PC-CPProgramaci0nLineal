import java.io.*;
import java.util.*;

public class LPProblemGenerator {

    public static void main(String[] args) {
        // Generar datos aleatorios
        Random rand = new Random();

        // Valores aleatorios para los coeficientes
        double coefX1 = rand.nextDouble() * 1000000;
        double coefX2 = rand.nextDouble() * 1000000;
        double coefX3 = rand.nextDouble() * 1000000;
        
        // Valores aleatorios para las restricciones
        double madera = rand.nextInt(500) + 100; // entre 100 y 600
        double metal = rand.nextInt(400) + 100;  // entre 100 y 500
        double manoObra = rand.nextInt(700) + 100; // entre 100 y 700
        double espacio = rand.nextInt(300) + 50;  // entre 50 y 350

        // Escribir los datos en el archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("lp_problem_generated.txt"))) {
            // Función objetivo
            writer.write("Maximizar Z = 210X1 + 240X2 + 130X3");
            writer.newLine();
            writer.write("Restricciones:");
            writer.newLine();
            writer.write("Madera: 5X1 + 2X2 + 4X3 <= " + madera);
            writer.newLine();
            writer.write("Metal: 3X1 + 5X2 + 2X3 <= " + metal);
            writer.newLine();
            writer.write("Mano de Obra: 6X1 + 8X2 + 4X3 <= " + manoObra);
            writer.newLine();
            writer.write("Espacio en Bodega: 2X1 + 2X2 + X3 <= " + espacio);
            writer.newLine();
            writer.write("Restricciones de No Negatividad: X1 >= 0, X2 >= 0, X3 >= 0");
            writer.newLine();

            System.out.println("Problema generado con éxito.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
