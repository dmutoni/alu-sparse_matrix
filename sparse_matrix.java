import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SparseMatrix {
    private int numRows;
    private int numCols;
    private Map<String, Integer> elements;

    public SparseMatrix(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.elements = new HashMap<>();
    }

    public void setElement(int row, int col, int value) {
        String key = row + "," + col;
        if (value != 0) {
            elements.put(key, value);
        } else {
            elements.remove(key);
        }
    }

    public int getElement(int row, int col) {
        String key = row + "," + col;
        return elements.getOrDefault(key, 0);
    }

    public SparseMatrix add(SparseMatrix otherMatrix) {
        SparseMatrix result = new SparseMatrix(this.numRows, this.numCols);

        for (Map.Entry<String, Integer> entry : this.elements.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            String[] indices = key.split(",");
            int row = Integer.parseInt(indices[0]);
            int col = Integer.parseInt(indices[1]);
            int otherValue = otherMatrix.getElement(row, col);
            result.setElement(row, col, value + otherValue);
        }

        for (Map.Entry<String, Integer> entry : otherMatrix.elements.entrySet()) {
            String key = entry.getKey();
            if (!this.elements.containsKey(key)) {
                String[] indices = key.split(",");
                int row = Integer.parseInt(indices[0]);
                int col = Integer.parseInt(indices[1]);
                result.setElement(row, col, entry.getValue());
            }
        }

        return result;
    }

    public SparseMatrix subtract(SparseMatrix otherMatrix) {
        SparseMatrix result = new SparseMatrix(this.numRows, this.numCols);

        for (Map.Entry<String, Integer> entry : this.elements.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            String[] indices = key.split(",");
            int row = Integer.parseInt(indices[0]);
            int col = Integer.parseInt(indices[1]);
            int otherValue = otherMatrix.getElement(row, col);
            result.setElement(row, col, value - otherValue);
        }

        for (Map.Entry<String, Integer> entry : otherMatrix.elements.entrySet()) {
            String key = entry.getKey();
            if (!this.elements.containsKey(key)) {
                String[] indices = key.split(",");
                int row = Integer.parseInt(indices[0]);
                int col = Integer.parseInt(indices[1]);
                result.setElement(row, col, -entry.getValue());
            }
        }

        return result;
    }

    public SparseMatrix multiply(SparseMatrix otherMatrix) {
        if (this.numCols != otherMatrix.numRows) {
            throw new IllegalArgumentException("Matrix dimensions do not match for multiplication.");
        }

        SparseMatrix result = new SparseMatrix(this.numRows, otherMatrix.numCols);

        for (Map.Entry<String, Integer> entry : this.elements.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();  
            String[] indices = key.split(",");
            int row = Integer.parseInt(indices[0]); 
            int col = Integer.parseInt(indices[1]);  

            for (int k = 0; k < otherMatrix.numCols; k++) {
                int otherValue = otherMatrix.getElement(col, k); 
                if (otherValue != 0) {
                    int currentVal = result.getElement(row, k); 
                    result.setElement(row, k, currentVal + value * otherValue); 
                }
            }
        }

        return result;
    }

    public static SparseMatrix fromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        int numRows = Integer.parseInt(reader.readLine().split("=")[1].trim());
        int numCols = Integer.parseInt(reader.readLine().split("=")[1].trim());
        SparseMatrix matrix = new SparseMatrix(numRows, numCols);

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("(")) {
                String[] parts = line.substring(1, line.length() - 1).split(",");
                int row = Integer.parseInt(parts[0].trim());
                int col = Integer.parseInt(parts[1].trim());
                int value = Integer.parseInt(parts[2].trim());
                matrix.setElement(row, col, value);
            }
        }

        reader.close();
        return matrix;
    }

    public void writeToFile(String outputPath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
        writer.write("Sparse Matrix (" + this.numRows + "x" + this.numCols + "):\n");

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                int value = getElement(i, j);
                if (value != 0) {
                    writer.write("(" + i + "," + j + ", " + value + ")\n");
                }
            }
        }

        writer.close();
    }

    public static void handleMatrixOperation(String operation, String file1, String file2) throws IOException {
        SparseMatrix matrix1 = SparseMatrix.fromFile(file1);
        SparseMatrix matrix2 = SparseMatrix.fromFile(file2);

        SparseMatrix resultMatrix;
        String outputFile;
        switch (operation) {
            case "add":
                resultMatrix = matrix1.add(matrix2);
                outputFile = "output_add.txt";
                break;
            case "subtract":
                resultMatrix = matrix1.subtract(matrix2);
                outputFile = "output_subtract.txt";
                break;
            case "multiply":
                resultMatrix = matrix1.multiply(matrix2);
                outputFile = "output_multiply.txt";
                break;
            default:
                throw new IllegalArgumentException("Invalid operation. Choose between add, subtract, or multiply.");
        }

        resultMatrix.writeToFile(outputFile);
        System.out.println("Result written to " + outputFile);
    }

    
    public static void main(String[] args) {
        System.out.println("......................................................");
        System.out.println("*                                                    *");
        System.out.println("*** Welcome to the Sparse Matrix Calculator! ***     *");
        System.out.println("*                                                    *");
        System.out.println("......................................................");
        System.out.println();
        System.out.println("=======================================================");
        System.out.println("||                Please select an operation:        ||");
        System.out.println("||               ---------------------------         ||");
        System.out.println("||  1. Add                                           ||");
        System.out.println("||  2. Subtract                                      ||");
        System.out.println("||  3. Multiply                                      ||");
        System.out.println("=======================================================");
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        System.out.print(">>> Enter the number of the operation (1/2/3): ");
        int choice = scanner.nextInt();

        String operation = null;

        switch (choice) {
            case 1:
                operation = "add";
                System.out.println("\nYou have selected: Addition\n");
                break;
            case 2:
                operation = "subtract";
                System.out.println("\nYou have selected: Subtraction\n");
                break;
            case 3:
                operation = "multiply";
                System.out.println("\nYou have selected: Multiplication\n");
                break;
            default:
                System.err.println("\nInvalid choice! Please select 1, 2, or 3.");
                System.exit(1);
        }

        String file1 = "matrixfile1.txt";
        String file2 = "matrixfile3.txt";

        try {
            System.out.println("   Processing your request. Please wait...");

            long startTime = System.nanoTime();

            SparseMatrix.handleMatrixOperation(operation, file1, file2);

            long endTime = System.nanoTime();
            
            long duration = (endTime - startTime) / 1_000_000_000;

            System.out.println("   Operation completed successfully!");
            System.out.println("   Time taken: " + duration + " seconds.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        scanner.close();
        System.out.println();
        System.out.println("Thank you for using the Sparse Matrix Calculator!");
        System.out.println("......................................................");
    }
}