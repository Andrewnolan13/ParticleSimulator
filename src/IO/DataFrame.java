package IO;
/*
 * This will just make iterating through the SolarSystem input data easier
 */

import java.util.HashMap;
import java.util.Iterator;

public class DataFrame implements Iterable<Row> {
    // store columns in a private hashmap   
    private HashMap<String, Column<?>> columnMap = new HashMap<>(); // store columns in a hashmap

    public int length;
    public int width;
    public String[] columns;

    public DataFrame(String[] data, @SuppressWarnings("rawtypes") Class[] types) {
        String[][] matrix = splitLines(data);
        String[][] slicedData = slice(matrix);

        for (int i = 0; i < types.length; i++){            
            @SuppressWarnings("rawtypes")
            Class type = types[i];
            if (type == Integer.class) {
                Column<Integer> column = new Column<>(slicedData[i], Integer.class);
                columnMap.put(column.name, column);
            } else if (type == Double.class) {
                Column<Double> column = new Column<>(slicedData[i], Double.class);
                columnMap.put(column.name, column);
            } else {
                throw new IllegalArgumentException("Unsupported type: " + type.getName());
            }
        }

        this.length = data.length-1;
        this.width = matrix[0].length;
        this.columns = new String[width];
        for (int i = 0; i < width; i++) {
            this.columns[i] = matrix[0][i];
        }
    }

    public Column<?> getColumn(String columnName) {
        return columnMap.get(columnName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String columnName : columns) {
            sb.append(columnName).append(columnName==columns[columns.length-1]?"\n":", ");
        }
        for(Row row : this) {
            sb.append(row).append("\n");
        }
        sb.append("length: ").append(length).append("\n");
        sb.append("width: ").append(width).append("\n");

        return sb.toString();
    }

    @Override
    public Iterator<Row> iterator() {
        return new Iterator<Row>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < length;
            }

            @Override
            public Row next() {
                Object[] rowValues = new Object[width];
                for (int i = 0; i < width; i++) {
                    String columnName = columns[i];
                    rowValues[i] = getColumn(columnName).values[index];
                }
                index++;
                return new Row(rowValues, columns);
            }
        };
    }    

    private static String[][] splitLines(String[] lines) {
        String[][] data = new String[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            data[i] = lines[i].split(",");
        }
        return data;
    }

    /* transforma a list of rows into a list of columns */
    private static String[][] slice(String[][] data) {
        String[][] slicedData = new String[data[0].length][data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                slicedData[j][i] = data[i][j];
            }
        }
        return slicedData;
    }
}


