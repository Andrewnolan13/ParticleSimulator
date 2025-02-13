package LinearAlgebra;
// import LinearAlgebra.Matrix;

public class Vector extends Matrix{
    
    public Vector(int size) {
        super(size, 1);
    }
    
    public Vector(Matrix m){
        //raise error if not m.getColumnCount() == 1;
        super(m.getRowCount(), m.getColCount());
        if(m.getColCount() != 1) {
            throw new IllegalArgumentException("Matrix must have only one column");
        }
        this.data = m.data;
    }
    
    public Vector(double[][] data) {
        super(data.length, 1);
        for (int i = 0; i < data.length; i++) {
            this.set(i, 0, data[i][0]);
        }
    }

    public double dot(Vector v) {
        if (this.getRowCount() != v.getRowCount()) {
            throw new IllegalArgumentException("Vectors must be of the same size");
        }
        double sum = 0;
        for (int i = 0; i < this.getRowCount(); i++) {
            sum += this.get(i, 0) * v.get(i, 0);
        }
        return sum;
    }
    // Matrix addition
    public static Vector add(Vector a,Vector b) {
        Matrix m = Matrix.add(a, b);
        return new Vector(m);
    }

    public Vector multiply(double scalar) {
        Vector result = new Vector(this.getRowCount());
        for (int i = 0; i < this.getRowCount(); i++) {
            result.set(i, 0, this.get(i, 0) * scalar);
        }
        return result;
    }

    public double get(int index) {
        return this.get(index, 0);
    }
    public Vector add(Vector v) {
        return add(this, v);
    }
    public void set(int index, double value) {
        this.set(index, 0, value);
    }
    
    public Vector copy(){
        double[][] dataCopy = new double[this.getRowCount()][this.getColCount()];
        for (int i = 0; i < this.getRowCount(); i++) {
            dataCopy[i][0] = this.get(i, 0);
        }
        return new Vector(dataCopy);
    }

    public double norm() {
        return Math.sqrt(this.dot(this));
    }
    public Vector normalize() {
        return this.multiply(1/this.norm());
    }
}
