package IO;

import java.util.Iterator;

/*
 * To be honest, these are quite redundant as I could just implemet Iterable<T> in Column and Row classes directly.
 * But I put them here to meet the requirements of the brief
 */
// public interface DataFrameInterface{}
public sealed interface DataFrameInterface<T extends Object> extends Iterable<T> permits DataFrameSubClass{
    public T[] getValues();
    
    @Override    
    public default Iterator<T> iterator(){
        return new Iterator<T>(){
            private int index = 0;

            public boolean hasNext() {
                return index < getValues().length;
            }
            
            public T next() {
                return getValues()[index++];
            }
        };
    }
    
}

sealed abstract class DataFrameSubClass<T extends Object> implements DataFrameInterface<T> permits Column, Row {
    public String name;
    public T[] values;

    public T[] getValues(){
        return this.values;
    };
}
