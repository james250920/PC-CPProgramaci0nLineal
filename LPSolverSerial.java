import java.io.*;
import java.util.*;

public class LPSolverSerial {

    public static void main(String[] args) {
        // Leer el archivo con los datos generados
        List<String> constraints = readConstraints("lp_problem_generated.txt");

        // Definir las variables y la función objetivo
        double maxZ = Double.NEGATIVE_INFINITY;
        double[] bestSolution = new double[3];
        
        // Probar combinaciones de X1, X2, X3
        for (double X1 = 0; X1 <= 100; X1++) {
            for (double X2 = 0; X2 <= 100; X2++) {
                for (double X3 = 0; X3 <= 100; X3++) {
                    if (isFeasible(X1, X2, X3, constraints)) {
                        double Z = 210 * X1 + 240 * X2 + 130 * X3;
                        if (Z > maxZ) {
                            maxZ = Z;
                            bestSolution[0] = X1;
                            bestSolution[1] = X2;
                            bestSolution[2] = X3;
                        }
                    }
                }
            }
        }
        
        System.out.println("Solución óptima (Serial):");
        System.out.println("X1 = " + bestSolution[0] + ", X2 = " + bestSolution[1] + ", X3 = " + bestSolution[2]);
        System.out.println("Valor de la función objetivo Z = " + maxZ);
    }

    // Método para leer las restricciones desde un archivo
    private static List<String> readConstraints(String fileName) {
        List<String> constraints = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isConstraintsSection = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Restricciones:")) {
                    isConstraintsSection = true;
                } else if (isConstraintsSection) {
                    constraints.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return constraints;
    }

    // Verificar si la solución cumple con las restricciones
    private static boolean isFeasible(double X1, double X2, double X3, List<String> constraints) {
        // Simulación simple de las restricciones (esto se puede mejorar con expresiones regulares)
        double madera = 5 * X1 + 2 * X2 + 4 * X3;
        double metal = 3 * X1 + 5 * X2 + 2 * X3;
        double manoObra = 6 * X1 + 8 * X2 + 4 * X3;
        double espacio = 2 * X1 + 2 * X2 + X3;
        
        // Verificar cada restricción
        for (String constraint : constraints) {
            if (constraint.contains("Madera")) {
                if (madera > 480) return false;
            }
            if (constraint.contains("Metal")) {
                if (metal > 350) return false;
            }
            if (constraint.contains("Mano de Obra")) {
                if (manoObra > 620) return false;
            }
            if (constraint.contains("Espacio en Bodega")) {
                if (espacio > 200) return false;
            }
        }
        return true;
    }
}
