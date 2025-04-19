package IO;

/*
 * allow int and double only
 */
final public class Column<T extends Number> extends DataFrameSubClass<T> {
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

}
