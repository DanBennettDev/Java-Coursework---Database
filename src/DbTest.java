import java.io.File;
import java.io.IOException;

/**
 * Tests for DbRecord Class
 */
public class DbTest {


    public static void run(){
        Tester t = new Tester();
        Boolean check = false;

        String subfolder = "testDB";

        // Reset folders for retest
        File directory = new File(subfolder);
        if(directory.exists()) {
            try {
                delete(directory);
            } catch(IOException e){
                throw new Error();
            }
        }

        Db.test();

        // Reset folders for retest
        if(directory.exists()) {
            try {
                delete(directory);
            } catch(IOException e){
                throw new Error();
            }
        }

        System.out.println("Testing: Records External");

        Db testDb = new Db(subfolder, subfolder);

        t.is(testDb.getFileCount(),0,"initialising class - no dbfiles at first to load from");

        testDb.AddTable("DbTest");

        t.is(testDb.getTableCount(),1, "add table successfully");

        testDb.getTable("DbTest").addField("String", DbTypeName.STRING, 30);
        testDb.getTable("DbTest").addField("Int", DbTypeName.INT);
        testDb.getTable("DbTest").addField("Float", DbTypeName.FLOAT);
        testDb.getTable("DbTest").addField("String2", DbTypeName.STRING, 30);

        testDb.getTable("DbTest").insertRecord("Row1","3", "3.3", "Cartouche");
        testDb.getTable("DbTest").insertRecord("Row2","4", "4.4", "Spizzle");
        testDb.getTable("DbTest").insertRecord("Row3","5", "-1.2", "Refried");
        testDb.getTable("DbTest").insertRecord("Row4","6", "6.6", "Klepto");

        try {
            testDb.saveTable("DbTest");
        } catch(Exception e){
            System.out.println("error - failed to save DbTest");
            throw new Error();
        }

        t.is(testDb.getFileCount(),1,"Saved table, so there should be 1 file");


        testDb.AddTable("DbTest2");
        testDb.getTable("DbTest2").addField("Strng", DbTypeName.STRING, 30);
        testDb.getTable("DbTest2").addField("Integer", DbTypeName.INT);
        testDb.getTable("DbTest2").addField("Fffloat", DbTypeName.FLOAT);
        testDb.getTable("DbTest2").addField("Strng2", DbTypeName.STRING, 30);

        testDb.getTable("DbTest2").insertRecord("Row 1","3", "3.3", "random");
        testDb.getTable("DbTest2").insertRecord("Row 2","4", "4.4", "words");
        testDb.getTable("DbTest2").insertRecord("Row 3","5", "-1.2", "different");
        testDb.getTable("DbTest2").insertRecord("Row 4","6", "6.6", "this time");

        t.is(testDb.getTableCount(), 2, "added 2nd table");

        try {
            testDb.saveTable("DbTest2");
        } catch(Exception e){
            System.out.println("error - failed to save DbTest");
            throw new Error();
        }
        t.is(testDb.getFileCount(),2,"Saved 2nd table, so there should be 2 files");

        //check structure loaded on init - file1
        t.is(testDb.getTable("DbTest").getFieldName(0), "String", "field name loads from file 1 correctly");
        t.is(testDb.getTable("DbTest").getFieldType(0), DbTypeName.STRING, "field type loads from file 1 correctly");
        t.is(testDb.getTable("DbTest").getFieldSize(0), 30, "field Size loads from file 1 correctly");
        // check structure loaded on init - file2
        t.is(testDb.getTable("DbTest2").getFieldName(0), "Strng", "field name loads from file 2  correctly");
        t.is(testDb.getTable("DbTest2").getFieldType(0), DbTypeName.STRING, "field type loads from file 2  correctly");
        t.is(testDb.getTable("DbTest2").getFieldSize(0), 30, "field Size loads from file 2  correctly");

        // load data
        testDb.loadTable("DbTest");
        t.is(testDb.getTable("DbTest").getValue(0,"String"), "Row1", "field data loads from file 1 correctly");
        testDb.loadTable("DbTest2");
        t.is(testDb.getTable("DbTest2").getValue(3,"Strng2"), "this time", "field data loads from file 2 correctly");

        // reinitialise file - should autoload from directory
        testDb = new Db(subfolder, subfolder);
        t.is(testDb.getFileCount(),2,"loads ");

        testDb.printFiles();

        t.is(testDb.getTable("DbTest").getFieldName(0), "String", "field name loads from file 1 correctly after reinit");
        t.is(testDb.getTable("DbTest").getFieldType(0), DbTypeName.STRING, "field type loads from file 1 correctly after reinit");
        t.is(testDb.getTable("DbTest").getFieldSize(0), 30, "field Size loads from file 1 correctly after reinit");
        t.is(testDb.getTable("DbTest2").getFieldName(0), "Strng", "field name loads from file 2  correctly after reinit");
        t.is(testDb.getTable("DbTest2").getFieldType(0), DbTypeName.STRING, "field type loads from file 2  correctly after reinit");
        t.is(testDb.getTable("DbTest2").getFieldSize(0), 30, "field Size loads from file 2  correctly after reinit");

        testDb.loadTable("DbTest");
        t.is(testDb.getTable("DbTest").getValue(0,"String"), "Row1", "field data loads from file 1 correctly after reinit");
        testDb.loadTable("DbTest2");
        t.is(testDb.getTable("DbTest2").getValue(3,"Strng2"), "this time", "field data loads from file 2 correctly after reinit");


        testDb.getTable("DbTest").insertRecord("Another","0", "0.4", "row");
        testDb.getTable("DbTest2").insertRecord("And Another","0", "0.4", "row");

        testDb.saveAllTables();

        testDb = new Db(subfolder, subfolder);
        testDb.loadAllTables();

        System.out.println(testDb.getTable("DbTest").getKeyNoIncrement());

        t.is(testDb.getTable("DbTest").getValue(4,"String"), "Another", "Save all and load all works file1");
        t.is(testDb.getTable("DbTest2").getValue(4,"Strng2"), "row", "Save all and load all works file2");


        t.results();




    }




    /* utility for testing only -
    taken from http://www.mkyong.com/java/how-to-delete-directory-in-java/ */
    public static void delete(File file)
            throws IOException {

        if(file.isDirectory()){
            if(file.list().length==0){
                file.delete();
                System.out.println("deleted dir: "
                        + file.getAbsolutePath());
            }else{
                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }

                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
            }

        }else{
            //if file, then delete it
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        run();
    }
}
