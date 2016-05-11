/**
 * Tests for DbFile Class
 */
public class DbFileTest {

    public static void run() {
        System.out.println("Testing: DbFile");

        DbFile.tests();

        Tester t = new Tester();
        System.out.println("Testing: DbFile - interface");
        Boolean check = false;

        DbFile dbf = new DbFile("test","tester");

        DbTable tab = new DbTable("animals");
        tab.addField("name", DbTypeName.STRING, 30);
        tab.addField("size", DbTypeName.STRING, 30);
        tab.addField("fierceness", DbTypeName.STRING, 30);


        tab.insertRecord("giraffe", "large", "moderate");
        tab.insertRecord("angry gopher", "small", "moderate");
        tab.insertRecord("calm gopher", "small", "negligible");
        tab.insertRecord("penguin", "smallish", "zero");

        try {
            dbf.writeToFile(tab);
        } catch(Exception e){
            System.out.println("ERROR!");
        }

        DbTable tab2 = new DbTable("animals2");
        tab2.addField("name", DbTypeName.STRING, 30);
        tab2.addField("size", DbTypeName.STRING, 30);
        tab2.addField("fierceness", DbTypeName.STRING, 30);


        dbf.readFromFile(tab2);
        t.is(tab2.getRowCount(),tab.getRowCount(),"readwrite resulted in same no of rows");
        t.is(tab2.getValue(0,"name"),"giraffe", "read write of table1");
        t.is(tab2.getValue(2,"fierceness"),"negligible", "read write of table2");
        t.is(tab2.getValue(3,"name"),"penguin", "read write of table3");

        dbf.readFromFile(tab);
        t.is(tab.getRowCount(),tab.getRowCount(),"readwrite same table resulted in same no of rows");


        DbTable tab3 = new DbTable("AnimalsLoadFromFile");

        dbf.readFromFile(tab3);
        t.is(tab3.getFieldName(0),"name","first field name loaded from file");
        t.is(tab3.getFieldType(1),DbTypeName.STRING,"second field type loaded from file");
        t.is(tab3.getFieldSize(2),30,"third field size loaded from file");
        t.is(tab3.getValue(3,"name"),"penguin", "read write 2 of table3");



        // delete records, save and reload to show keys are invariant under save/reload
        // while we're at it, also test load of structure alone
        tab3.deleteRecord(0);
        tab3.deleteRecord(1);

        try {
            dbf.writeToFile(tab3);
        }catch (Exception e){
            System.out.println("error writing to file");
            throw new Error();
        }

        DbTable tab4 = new DbTable("AnimalsTestStructureLoad");
        try {
            dbf.readStructureFromFile(tab4);
        } catch(Exception e){
            System.out.println("ERROR loading structure");
            throw new Error();
        }
        t.is(tab4.getFieldName(0),"name","first field name loaded from file");
        t.is(tab4.getFieldType(1),DbTypeName.STRING,"second field type loaded from file");
        t.is(tab4.getFieldSize(2),30,"third field size loaded from file");

        dbf.readFromFile(tab4);
        t.is(tab4.getValue(2,"name"),"calm gopher", "keys not altered by delete, save & reload 1");
        t.is(tab4.getValue(3,"name"),"penguin", "keys not altered by delete, save & reload 2");







        t.results();




    }


    public static void main(String[] args) {
        run();
    }
}
