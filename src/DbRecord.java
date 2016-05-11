import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DbRecord Class for database
 * Daniel Bennett
 * 24/02/2016
 */

public class DbRecord {

    private final char DELIMITER = 0x0241E;
    private final char ENDRECORD = 0x0000A;

    private int key;
    private List<DbType> data;

    DbRecord(int key, List<DbField> fields){
        this.data = new ArrayList<>();
        DbType field;
        int size;
        DbTypeName type;
        for(int i=0; i<fields.size(); i++){
            type = fields.get(i).getType();
            size = fields.get(i).getSize();
            field = toDbType(type, size, "0");
            data.add(field);
        }
        this.key = key;
    }

    // instantiate with a list of values
    DbRecord(int key, List<DbField> fields, String ... values)  {
        if(fields.size() != values.length){
            System.out.println("Error: String inputs: "+values.length + ", fields : " +fields.size());
            throw new IllegalArgumentException();
        }
        this.data = new ArrayList<>(values.length);
        int size;
        DbTypeName type;
        for(int i=0; i<values.length; i++){
            type = fields.get(i).getType();
            size = fields.get(i).getSize();
            this.data.add(i,toDbType(type, size, stripDelimeters(values[i])));
        }
        this.key=key;
    }

    //remove instances of the values used to delimit fields and lines in the DbFile Class
    private String stripDelimeters(String in){
        if(in.contains(""+DELIMITER)){
            in = in.replace(""+DELIMITER, "");
        }
        if(in.contains(""+ENDRECORD)){
            in = in.replace(""+ENDRECORD, "");
        }
        return in;
    }

    // convert incoming string to the specified data type
    private DbType toDbType(DbTypeName type, int size, String value ){
        switch(type){
            case INT:
                return new DbInt(value);
            case FLOAT:
                return new DbFloat(value);
            case STRING:
                return new DbString(size, value);
            default:
                System.out.println("invalid field type " + type);
                throw new IllegalArgumentException();
        }
    }

    // return value in field as string
    public String getAsString(int i){
        if(i>data.size()){
            throw new ArrayIndexOutOfBoundsException();
        }
        return data.get(i).print();
    }

    // get key for the record
    public int getKey(){
        return key;
    }

    // get the value stored in the requested field index
    public Object getVal(int i){
        if(i>data.size()){
            throw new ArrayIndexOutOfBoundsException();
        }
        return data.get(i).get();
    }

    // get the field at requested index as DbType
    public DbType getField(int i){
        if(i>data.size()){
            throw new ArrayIndexOutOfBoundsException();
        }
        return data.get(i);
    }


    // get size of the record - no of fields
    public int size(){
        return data.size();
    }


    // set the field to value specified by string
    public void set(int i, String val){
        if(i>= data.size()){
            throw new IndexOutOfBoundsException();
        }
        data.get(i).set(stripDelimeters(val));
    }

    // set the field to value specified by int
    public void set(int i, int val){
        if(i>= data.size()){
            throw new IndexOutOfBoundsException();
        }
        data.get(i).set(val);
    }

    // set the field to value specified by double
    public void set(int i, float val){
        if(i>= data.size()){
            throw new IndexOutOfBoundsException();
        }
        data.get(i).set(val);
    }



}
