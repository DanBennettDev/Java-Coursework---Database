/**
 * Interface class for database types - provides unified interface for eg. Ints, Strings, Floats
 * allows mingling of these types in a single record.
 */
public interface  DbType {
    public Object get();
    public int getSize();
    public String print();
    public void   set(Object val);
    public DbTypeName getType();

}
