/**
 * Tests for DbTable Class
 */

public class DbTableTest {


    public static void run() {
        Tester t = new Tester();
        Boolean check =false;

        DbTable.tests();








        System.out.println("Testing: DbTable - external");

        //init
        DbTable tab = new DbTable("animals");
        tab.addField("name", DbTypeName.STRING, 30);
        tab.addField("size", DbTypeName.STRING, 30);
        tab.addField("fierceness", DbTypeName.STRING, 30);

        t.is(tab.getName(), "animals", "Test init and getname");

        t.is(tab.getFieldNameExists("size"), true, "getFieldNameExists works for true" );
        t.is(tab.getFieldNameExists("bertram"), false, "getFieldNameExists works for false" );

        check=false;
        try{tab.addField("name", DbTypeName.STRING, 30);} catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "Cannot add duplicate field name");


        // getFieldName
        t.is(tab.getFieldName(0), "name", "Test init and getFieldName");
        t.is(tab.getFieldName(2), "fierceness", "Test init and getFieldName");
        check=false;
        try{tab.getFieldName(-1);} catch(IndexOutOfBoundsException e){
            check=true;
        }
        t.is(check, true, "Test getFieldName <0");
        check=false;
        try{tab.getFieldName(3);} catch(IndexOutOfBoundsException e){
            check=true;
        }
        t.is(check, true, "Test getFieldName > size");

        // getFieldNo()
        t.is(tab.getFieldNo("fierceness"),2, "test getFieldNo valid");

        check=false;
        try{tab.getFieldNo(null);} catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "test getFieldNo null");

        check=false;
        try{tab.getFieldNo("bert");} catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "test getFieldNo invalid");

        // getFieldNames()
        t.is(tab.getFieldNames().get(0), "name", "test getFieldNames 1");
        t.is(tab.getFieldNames().get(1), "size", "test getFieldNames 2");
        t.is(tab.getFieldNames().get(2), "fierceness", "test getFieldNames 3");

        check=false;
        try{tab.getFieldNames().get(3);} catch(IndexOutOfBoundsException e){
            check=true;
        }
        t.is(check, true, "test getFieldNames 3");

        // getFieldCount()
        t.is(tab.getFieldCount(), 3, "getFieldCount sized");

        // setFieldName()
        tab.setFieldName("name", "THENAME");
        t.is(tab.getFieldName(0), "THENAME", "setFieldName - valid field name");
        tab.setFieldName(0, "AnimalName");
        t.is(tab.getFieldName(0), "AnimalName", "setFieldName - valid field no");

        check=false;
        try{tab.setFieldName("notThere", "arr");} catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "setFieldName - invalid field name throws exception");

        check=false;
        try{tab.setFieldName("AnimalName", "fierceness");}
        catch(IllegalArgumentException e){check=true; }
        t.is(check, true, "setFieldName - duplicate field name throws exception");

        check=false;
        try{tab.setFieldName(10, "arr");}
        catch(IllegalArgumentException e){check=true;}
        t.is(check, true, "setFieldName - invalid field No throws exception");

        check=false;
        try{tab.setFieldName(-1, "arr");} catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "setFieldName - invalid field No throws exception");

        tab.setFieldName(0, "name");
        t.is(tab.getFieldName(0), "name", "setFieldName - valid field no");

        // insert record & get record
        tab.insertRecord("giraffe", "large", "low");
        tab.insertRecord("angry gopher", "small", "moderate");
        tab.insertRecord("calm gopher", "small", "negligible");
        tab.insertRecord("penguin", "smallish", "zero");

        t.is(tab.getRecord(0).getVal(0), "giraffe", "insertRecord and getRecord - both valid0");
        t.is(tab.getRecord(1).getVal(1), "small", "insertRecord and getRecord - both valid1");
        t.is(tab.getRecord(2).getVal(2), "negligible", "insertRecord and getRecord - both valid2");
        t.is(tab.getRecord(3).getVal(1), "smallish", "insertRecord and getRecord - both valid3");

        check=false;
        try{tab.getRecord(7);} catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "getRecord - invalid field>bound No throws exception");

        check=false;
        try{tab.getRecord(-3);} catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "getRecord - invalid field No<0 throws exception");


        // getValue
        t.is(tab.getValue(0,"fierceness"), "low", "getValue valid row and field 1");
        t.is(tab.getValue(2,"name"), "calm gopher", "getValue valid row and field 2");

        check=false;
        try{tab.getValue(2,"bert");} catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "getValue - invalid field name throws exception");


        t.is(tab.getValue(11,"name"), null, "getValue - invalid key too large returns -1");
        t.is(tab.getValue(-5,"fierceness"), null, "getValue - invalid key <0 returns -1");



        // row count returns valid num
        t.is(tab.getRowCount(), 4, "getRowCount returns valid numnber >0");

        // delete record
        tab.deleteRecord(1);
        t.is(tab.getValue(1,"name"), null, "deleteRecord with valid number works");

        tab.deleteRecord(-1);
        t.is(tab.getValue(0,"name"), "giraffe", "deleteRecord 0 has no effect (prints \"no effect\"");
        tab.deleteRecord(5);
        t.is(tab.getValue(3,"name"), "penguin", "deleteRecord 0 has no effect (prints \"no effect\"");

        //try to add new field now table has records
        check=false;
        try {
            tab.addField("FIELDTEST", DbTypeName.FLOAT);
        } catch (Exception e){
            check=true;
        }
        t.is(check, true, "prevents adding new field when table has rows");

        t.results();
    }

    public static void main(String[] args) {
        run();
    }
}
