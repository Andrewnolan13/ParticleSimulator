package IO;

import java.util.Iterator;
/*
 * allow int and double only
 */
public class Column<T extends Number> implements Iterable<T>{
    public String name;
    public T[] values;

    @SuppressWarnings("unchecked")
    public Column(String[] column, Class<T> type) {
        this.name = column[0];
        this.values = (T[]) java.lang.reflect.Array.newInstance(type, column.length - 1);
        for (int i = 1; i < column.length; i++) {
            if (type == Integer.class) {
                values[i - 1] = type.cast(Integer.parseInt(column[i]));
            } else if (type == Double.class) {
                values[i - 1] = type.cast(Double.parseDouble(column[i]));
            } else {
                throw new IllegalArgumentException("Unsupported type: " + type.getName());
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < values.length;
            }

            @Override
            public T next() {
                return values[index++];
            }
        };
    }

}
