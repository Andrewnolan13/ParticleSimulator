package LinearAlgebra;

public class Matrix {
    public double[][] data;

    // Constructor to initialize a matrix
    public Matrix(int rows, int cols) {
        data = new double[rows][cols];
    }

    // Set value at a specific position in the matrix
    public void set(int row, int col, double value) {
        data[row][col] = value;
    }

    // Get value at a specific position in the matrix
    public double get(int row, int col) {
        return data[row][col];
    }

    // Get number of rows
    public int getRowCount() {
        return data.length;
    }

    // Get number of columns
    public int getColCount() {
        return data[0].length;
    }

    // Matrix addition
    public static Matrix add(Matrix a, Matrix b) {
        if (a.getRowCount() != b.getRowCount() || a.getColCount() != b.getColCount()) {
            throw new IllegalArgumentException("Matrices must be of the same size");
        }
        Matrix result = new Matrix(a.getRowCount(), a.getColCount());
        for (int i = 0; i < a.getRowCount(); i++) {
            for (int j = 0; j < a.getColCount(); j++) {
                result.set(i, j, a.get(i, j) + b.get(i, j));
            }
        }
        return result;
    }

    // Matrix multiplication
    public static Matrix multiply(Matrix a, Matrix b) {
        if (a.getColCount() != b.getRowCount()) {
            throw new IllegalArgumentException("Incompatible matrix sizes for multiplication");
        }
        Matrix result = new Matrix(a.getRowCount(), b.getColCount());
        for (int i = 0; i < a.getRowCount(); i++) {
            for (int j = 0; j < b.getColCount(); j++) {
                double sum = 0;
                for (int k = 0; k < a.getColCount(); k++) {
                    sum += a.get(i, k) * b.get(k, j);
                }
                result.set(i, j, sum);
            }
        }
        return result;
    }

    // Matrix transpose
    public Matrix transpose() {
        Matrix result = new Matrix(this.getColCount(), this.getRowCount());
        for (int i = 0; i < this.getRowCount(); i++) {
            for (int j = 0; j < this.getColCount(); j++) {
                result.set(j, i, this.get(i, j));
            }
        }
        return result;
    }

    // Print matrix for visualization
    public void print() {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                System.out.print(data[i][j] + " ");
            }
            System.out.println();
        }
    }
}

