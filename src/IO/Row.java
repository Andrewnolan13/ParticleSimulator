package IO;

import java.util.HashMap;
import java.util.Iterator;

final public class Row extends DataFrameSubClass<Object> {
    public Object[] values;
    public String[] columnNames;
    private HashMap<String, Object> map = new HashMap<>();
    
    public Row(Object[] values, String[] columnNames) {
        this.columnNames = columnNames;
        this.values = values;
        for (int i = 0; i < columnNames.length; i++) {
            map.put(columnNames[i], values[i]);
        }
    }

    public Object getValue(String columnName) {
        return map.get(columnName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i]).append(i == values.length - 1 ? "" : ", ");
        }
        return sb.toString();
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < values.length;
            }

            @Override
            public Object next() {
                return values[index++];
            }
        };
    }
}