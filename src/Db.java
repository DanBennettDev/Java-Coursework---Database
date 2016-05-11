import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;

/**
 *  Database class - collects together a set of tables and their file storage
 *  May subsequently acrue other functions
 *
 *  ISSUE: does not prevent adding of multiple tables with same name. This is fixed
 *  in the DbCache class which will take over handling of the table list.
 */

public class Db {


    private String name;
    private String dbFolder;
    private List<DbTable> tables; // TODO - remove on incorporating DbCache into this class
    private List<DbFile> dbFiles; // TODO - remove on incorporating DbCache into this class
    private DbFile fileInfo; // TODO - remove on incorporating DbCache into this class


    Db(String name, String dbFolder){
        this.name = name;
        this.dbFolder = dbFolder;
        this.tables = new ArrayList<>();
        this.dbFiles = new ArrayList<>();

        this.fileInfo = new DbFile("", "");
        try {
            fileInfo.createFolderIfNotExist(this.dbFolder);
        } catch(FileSystemException e) {
            System.exit(1);
        }
        scanForDbs();

    }
    // outputs list of files in Database as comma delimited string
    public void printFiles(){
        for(int i=0; i<dbFiles.size(); i++){
            System.out.print(dbFiles.get(i).getName() + ", ");
        }
        System.out.print("\n");
    }

    // outputs list of files in database as ArrayList.
    public List<String> listFiles(){
        List<String> files = new ArrayList<>();

        for(int i=0; i<dbFiles.size(); i++){
            files.add(dbFiles.get(i).getName());
        }
        return files;
    }

    // outputs list of tables in Database as comma delimited string
    public void printTables(){
        for(int i=0; i<tables.size(); i++){
            System.out.print(tables.get(i).getName() + ", ");
        }
        System.out.print("\n");
    }

    // outputs list of tables in Database as ArrayList
    public List<String> listTables(){
        List<String> tlist = new ArrayList<>();

        for(int i=0; i<tables.size(); i++){
            tlist.add(this.tables.get(i).getName());
        }
        return tlist;
    }



    // TODO - remove this method when I incorporate cache (where it is duplicated)
    // scan the dbFolder for database files, set up list of dbFiles, load structures of tables to table list
    private void scanForDbs(){
        File dir = new File(dbFolder);
        DbFile thisDbF;
        String ext = this.fileInfo.getFileExtension();
        if(!dir.exists()) {
            System.out.println("no directory "+ this.dbFolder);
            throw new IllegalArgumentException();
        }
        FilenameFilter filter = (dir1, filename) -> filename.endsWith(ext);
        File[] files = dir.listFiles(filter);

        for(File f: files){
            thisDbF = new DbFile(dbFolder, f.getName().replace(ext, ""));
            dbFiles.add(thisDbF);
            DbTable tab = new DbTable(thisDbF.getName());
            try {
                thisDbF.readStructureFromFile(tab);
                tables.add(tab);
            } catch(Exception e) {
                System.out.println("error reading structure of "+ tab.getName());
                throw new Error();
            }

        }
    }

    // returns the named table as DbTable object
    public DbTable getTable(String name){
        DbTable tab;
        for(int i=0; i<this.tables.size(); i++) {
            tab=this.tables.get(i);
            if(tab.getName().equals(name)) {
                return tab;
            }
        }
        return null;
    }
    // returns count of tables in db
    public int getTableCount(){return this.tables.size();};
    // returns count of files in db
    public int getFileCount(){return this.dbFiles.size();}


    // Saves table to file. File is named tablename.extension (extension defined in DbFile class)
    // creates file if no file already exists
    public void saveTable(String name) throws Exception{
        DbTable tab = getTable(name);
        if(tab==null){
            System.out.println("no such table");
            throw new IllegalArgumentException();
        }
        boolean CreateIfNotExist = true;
        DbFile file = getFile(name, CreateIfNotExist);
        try {
            file.writeToFile(tab);
            return;
        } catch(Exception e) {
            System.out.println("failed to save db " +name);
            throw e;
        }
    }

    // saves all tables in database to files named <tablename>.extension (extension defined in dbFile)
    public void saveAllTables(){
        for(int i=0; i<tables.size(); i++){
            try {
                saveTable(tables.get(i).getName());
            } catch (Exception e){

            }
        }
    }

    // loads all tables in database from their files (named <tablename>.extension (extension defined in dbFile)
    public void loadAllTables(){
        for(int i=0; i<dbFiles.size(); i++){
            loadTable(dbFiles.get(i).getName());
        }
    }

    // loads a single table from its file (named <tablename>.extension (extension defined in dbFile)
    public void loadTable(String name){
        DbFile file = getFile(name, false);
        if(file==null){
            System.out.println("no such file, "+name+" cannot load table");
        }
        DbTable tab = getTable(name);
        if(tab==null){
            tab = new DbTable(name);
            this.tables.add(tab);
        }
        file.readFromFile(tab);

    }


    // TODO - remove when switch to CACHE (duplication)
    // returns file corresponding to table name. Looks in list of DbFiles for DbFile
    // if it cannot find the file there, it creates a new file and adds to list.
    // Assumes ScanForDbs has already been run
    private DbFile getFile(String name, boolean createIfNotFound){
        for(int i=0; i<this.dbFiles.size(); i++){
            if(this.dbFiles.get(i).getName().equals(name)){
                return this.dbFiles.get(i);
            }
        }
        if(createIfNotFound) {
            DbFile newFile = new DbFile(dbFolder, name);
            this.dbFiles.add(newFile);
            return newFile;
        }
        return null;
    }


    // adds a new table to the database's table list. Only name at this point
    // structure and data must be added using methods in DbTable.
    // TODO: currently does not prevent addition of multiple tables with same name
    // this is fixed in the DbCache version, which will replace this
    public void AddTable(String name){
        DbTable t = new DbTable(name);
        tables.add(t);
    };



    public static void test(){
        Tester t = new Tester();
        Boolean check = false;
        System.out.println("Testing: Records Internal");

        //delete subfolder in case of rerunning tests

        Db testDb = new Db("testDB", "testDB");

        testDb.scanForDbs();

        t.is(testDb.dbFiles.size(),0,"initialising class - no dbfiles at first to load from");

        testDb.AddTable("DbTest");

        t.is(testDb.tables.size(),1, "add table successfully");

        testDb.tables.get(0).addField("String", DbTypeName.STRING, 30);
        testDb.tables.get(0).addField("Int", DbTypeName.INT);
        testDb.tables.get(0).addField("Float", DbTypeName.FLOAT);
        testDb.tables.get(0).addField("String2", DbTypeName.STRING, 30);

        testDb.tables.get(0).insertRecord("Row1","3", "3.3", "Cartouche");
        testDb.tables.get(0).insertRecord("Row2","4", "4.4", "Spizzle");
        testDb.tables.get(0).insertRecord("Row3","5", "-1.2", "Refried");
        testDb.tables.get(0).insertRecord("Row4","6", "6.6", "Klepto");



        t.results();
    }

}
