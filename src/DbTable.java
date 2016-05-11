import java.util.ArrayList;
import java.util.List;

/**
 * DbTable class for database
 * due to complexity of parameters required to initialise a table (particularly once types are added)
 * tables are instantiated with only a name, and structure & data are added subsequently.
 * I felt this would be best moved to a more compact single step instantiation format later when
 * a text/graphical interface is added.
 * for the moment this requires caution that the interface does not allow users to change
 * table format after records are added (or I will need to add a method for revising all existing records
 * when structure changes occur)
 *
 */


public class DbTable {

    private String name;
    private List<DbField> fields;
    private List<DbRecord> records;
    private int nextKey;

    DbTable(String name){
        this.fields= new ArrayList<>();
        this.name = name;
        this.records = new ArrayList<>();
        this.nextKey = 0;
    }

    // adds a field to a table. Size is only currently required for Strings -
    // size is defaulted to arbitrary value  for other data types, regardless of input
    public void addField(String name, DbTypeName type, int size){
        if(getFieldNameExists(name)){
            throw new IllegalArgumentException();
        }
        if(getRowCount()>0){
            System.out.println("can't add extra fields to Table with records");
            throw new IllegalArgumentException();
        }
        DbField f = new DbField(name, type, size);
        fields.add(f);
    }

    // add a field without giving size. Size is defaulted to arbitrary data type for non-string
    // fields, or to defined default string size for Strings.
    public void addField(String name, DbTypeName type){
        addField(name, type, 0);
    }

    public boolean getFieldNameExists(String name){
        for(int i=0 ; i<getFieldCount(); i++){
            if(getFieldName(i).equals(name)){
                return true;
            }
        }
        return false;
    };


    // returns key for the next record and increments key ready for next read.
    // this ensures uniqueness of key in table. Rudimentary but works for now.
    // may wish to revise later in response to demands
    public int getNextKey(){
        return nextKey++;
    }

    // these next two are required for load from file.
    // Ideally these wouldn't be public since they make handling of key uniqueness more fragile
    // and clutter the interface.
    // TODO: think about better ways of handling this.
    public void setKey(int key){
        this.nextKey =key;
    }
    public int getKeyNoIncrement(){
        return nextKey;
    }

    public String getName(){
        return name;
    }

    // returns number of records in table
    public int getRowCount(){
        return this.records.size();
    }

    // returns number of fields in table
    public int getFieldCount(){
        return this.fields.size();
    }

    // returns List of fieldnames
    public List<String> getFieldNames(){
        List<String> out = new ArrayList<>();
        for(int i=0; i<this.fields.size(); i++){
            out.add(fields.get(i).getName());
        }
        return out;
    }

    // returns List of fieldtypes
    public List<DbTypeName> getFieldTypes(){
        List<DbTypeName> out = new ArrayList<>();
        for(int i=0; i<this.fields.size(); i++){
            out.add(fields.get(i).getType());
        }
        return out;
    }

    // returns List of field sizes
    public List<Integer> getFieldSizes(){
        List<Integer> out = new ArrayList<>();
        for(int i=0; i<this.fields.size(); i++){
            out.add(fields.get(i).getSize());
        }
        return out;
    }

    // returns list of fields
    public List<DbField> getFields(){
        return fields;
    }

    // get name of numbered field. Throws exception if fieldNo does not exist in table
    public String getFieldName(int fieldNo) {
        if(fieldNo>=this.fields.size() || fieldNo<0) {
            throw new IndexOutOfBoundsException();
        }
        return fields.get(fieldNo).getName();
    }

    // get type of numbered field. Throws exception if fieldNo does not exist in table
    public DbTypeName getFieldType(int fieldNo) {
        if(fieldNo>=this.fields.size() || fieldNo<0) {
            throw new IndexOutOfBoundsException();
        }
        return fields.get(fieldNo).getType();
    }

    // get size of numbered field. Throws exception if fieldNo does not exist in table
    public int getFieldSize(int fieldNo) {
        if(fieldNo>=this.fields.size() || fieldNo<0) {
            throw new IndexOutOfBoundsException();
        }
        return fields.get(fieldNo).getSize();
    }

    // get number of named field.
    public int getFieldNo(String name){
        for(int i=0; i< this.fields.size(); i++){
            if(fields.get(i).getName().equals(name)){
                return i;
            }
        }
        throw new IllegalArgumentException();
    }

    // set numbered field to new name, disallows duplication of names
    public void setFieldName(int i, String name){
        if(i>fields.size()||i<0){
            throw new IllegalArgumentException();
        }
        for(DbField item : fields){
            if(item.getName().equals(name)){
                throw new IllegalArgumentException();
            }
        }
        fields.get(i).setName(name);
    }

    // set given field name to new name, disallows duplication of names
    public void setFieldName(String fieldName, String newName) {
        if(fieldName.equals(newName)){return; }
        int i=0, size=this.fields.size();
        while(!fields.get(i).getName().equals(fieldName)){
            i++;
            if(i==size){
                throw new IllegalArgumentException();
            }
        }
        setFieldName(i, newName);

    }

    // inserts record into table based on array of strings. Array must be >= length as No of fields in table
    // extra strings will be ignored.
    // TODO: consider if ignoring extra strings is best approach or should throw exception. Causes no problems for now.
    public void insertRecord(String ... values){
        if(values.length < this.fields.size()){
            throw new IllegalArgumentException();
        }
        DbRecord rec = new DbRecord(getNextKey(), fields, values);
        this.records.add(rec);
    }

    // inserts record into table. Record must have same number of fields of same sizes and types as existing records
    // TODO: handle insert where string of smaller size. Should be acceptable & Will be useful later whan moving data between tables
    public void insertRecord(DbRecord r){
        if(r.size()==this.getFieldCount()){
            for(int i=0; i<this.getFieldCount(); i++){
                if(r.getField(i).getType()!=fields.get(i).getType()
                        || r.getField(i).getSize()!= fields.get(i).getSize()){
                    throw new IllegalArgumentException();
                }
            }
            this.records.add(r);
        } else {
            throw new IllegalArgumentException();
        }
    }

    // returns index of given key in the list of records in table.
    private int getKeyRow(int key){
        if(key>=nextKey || key <0){
            return -1;
        }
        int left=0, right=this.records.size()-1, mid = ((right-left)/2)+left;
        int val;
        while ( (val=this.records.get(mid).getKey()) != key && left < right) {
            if(val < key) {left=mid+1;}
            if(val > key) {right=mid-1;}
            mid = ((right-left)/2)+left;
        }
        if(val!=key){
            return -1;
        }
        return mid;
    }

    // deletes record with given key
    public void deleteRecord(int key){
        int row = getKeyRow(key);
        if(row>=0 && row < this.records.size()){
            this.records.remove(row);
            return;
        }
        System.out.println("not found");
        return;
    }

    public void truncate(){
        for(int i=0; i<getRowCount(); i++){
            deleteRecord(i);
        }
        this.nextKey=0;
    }

    // returns record with given key
    public DbRecord getRecord(int key){
        int row = getKeyRow(key);
        if(row<records.size() && row>=0){
            return records.get(row);
        }
        throw new IllegalArgumentException();
    }

    // returns complete list of records
    public List<DbRecord> getRecords(){
        return records;
    }

    // gets the value in a particular primary key and field name
    public Object getValue(int key, String field){
        int fieldNo = getFieldNo(field);
        return getValue(key, fieldNo);
    }

    // returns value at a particular primary key and field No
    public Object getValue(int key, int fieldNo){
        int row = getKeyRow(key);
        if (fieldNo != -1 && row<records.size() && row >= 0) {
            return records.get(row).getVal(fieldNo);
        }
        return null;
    }

    public static void tests() {
        Tester t = new Tester();
        Boolean check = false;
        System.out.println("Testing: DbTable - internal tests");

        //init
        DbTable tab = new DbTable("animals");
        tab.addField("name", DbTypeName.STRING, 30);
        tab.addField("size", DbTypeName.STRING, 30);
        tab.addField("fierceness", DbTypeName.STRING, 30);

        tab.insertRecord("giraffe", "large", "moderate");
        tab.insertRecord("angry gopher", "small", "moderate");
        tab.insertRecord("calm gopher", "small", "negligible");
        tab.insertRecord("penguin", "smallish", "zero");

        t.is(tab.records.get(0).getKey(), 0, "check keys applied correctly 1");
        t.is(tab.records.get(1).getKey(), 1, "check keys applied correctly 2");
        t.is(tab.records.get(2).getKey(), 2, "check keys applied correctly 3");
        t.is(tab.records.get(3).getKey(), 3, "check keys applied correctly 4");

        t.is(tab.getKeyRow(0), 0, "check getKeyRow returns right row for first key");
        t.is(tab.getKeyRow(1), 1, "check getKeyRow returns right row for key 1");
        t.is(tab.getKeyRow(2), 2, "check getKeyRow returns right row for key 2");
        t.is(tab.getKeyRow(3), 3, "check getKeyRow returns right row for key 3");

        tab.records.remove(1);
        System.out.println(tab.nextKey);

        t.is(tab.getKeyRow(1), -1, "check getKeyRow returns right row for key 1 after removal");
        t.is(tab.getKeyRow(2), 1, "check getKeyRow returns right row for last key after removal");
        t.is(tab.getKeyRow(3), 2, "check getKeyRow returns right row for first key after removal");
        t.is(tab.getKeyRow(0), 0, "check getKeyRow returns right row for last key after removal");


        t.results();
    }
}









