
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.FileSystemException;
import java.util.*;


/**
 *  Cache class for database-
 *  handles interraction between memory and file system for the database
 *  collects together a set of tables and their file storage
 *  moves tables into memory when they are required, and removes
 *  less recently used tables from memory, to keep amount of memory used
 *  under the cacheSize parameter
 */

// ToDo:
//		- issue: at present if single table exceeds cachesize this table cannot be used
//		    and the system will not handle this gracefully. Add handling & consider solutions.
// 		- update size etc. when inserting/deleting record
//			(therefore all table operations will need to go through this class)
//		- Move relevant functions (save, load) here from Db Class
// 		- rewrite remaining Db functions so that they interface with tables via this class
//		- in DBFile, store row count, file size, etc. with table to allow querying metadata without loading
// 		- Creating a table at present is cumbersome - too many parameters required to handle with one function
//			but when a text gui is created this can be handled by parsing a string into the various commands
//			required - from create table, to add columns to "finalise"



public class DbCache {

	private List<CacheTable> cache;
    private List<DbFile> dbFiles;
	private String dbFolder;
    private int cacheSize;
    // TODO DbFile here allows access to constants in that class to avoid repetition.
    // but is there a better way?
    private DbFile fileInfo;


    DbCache(int cacheSize, String dbFolder){
    	this.cache = new LinkedList<>();
    	this.dbFiles = new ArrayList<>();
    	this.cacheSize = cacheSize;
    	this.fileInfo = new DbFile("test", "test");
		this.dbFolder = dbFolder;
		try {
			fileInfo.createFolderIfNotExist(this.dbFolder);
		} catch(FileSystemException e) {
			System.exit(1);
		}
		scanForDbs();
    }

	// scan the dbFolder for database files, set up list of dbFiles, load structures of tables to table list
	private void scanForDbs(){
	    File dir = new File(this.dbFolder);
	    DbFile thisDbF;
	    String ext = this.fileInfo.getFileExtension();
	    if(!dir.exists()) {
	        System.out.println("no directory "+ this.dbFolder);
	        throw new IllegalArgumentException();
	    }

	    FilenameFilter filter = (dir1, filename) -> filename.endsWith(ext);

	    File[] files = dir.listFiles(filter);

	    int i=0;
	    for(File f: files){
	        thisDbF = new DbFile(dbFolder, f.getName().replace(ext, ""));
	        dbFiles.add(thisDbF);
	        CacheTable ct = new CacheTable(thisDbF.getName());
	        try {
	            thisDbF.readStructureFromFile(ct.getTable());
	            ct.setRowSize();
	            cache.add(0, ct);
	        } catch(Exception e) {
	            System.out.println("error reading structure of "+ ct.getName());
	            throw new Error();
	        }

	    }
	}

	// set the limit (bytes) beyond which tables will not be stored in memory
    public void setCacheSize(int size){
    	this.cacheSize=size;
    }

	// add a table to the cache - just name at this point, requires adding of
	// table structure via the
    public void addTable(String name){
    	if(getCacheTable(name)!=null){
    		System.out.println("cannot add table, name "+name+"already exists");
    		throw new IllegalArgumentException();
    	}
        CacheTable t = new CacheTable(name);
        cache.add(0,t);
    };

	// returns table as CacheTable object - including details relevant to its
	// cacheing - eg size
    public CacheTable getCacheTable(String name){
    	for(int i=0; i<cache.size(); i++){
    		if(cache.get(i).getName().equals(name)){
    			return cache.get(i);
    		}
    	}
    	return null;
    }


	// below are sketches - largely unfinished


    // TODO - complete this.
    // to be called once all columns have been added sets rowsize, sets isInmemory flag, saves to file
    public void finalizeTableStructure(CacheTable tab){
    	tab.setRowSize();
		tab.setIsInMemory(true);
		saveTable(tab);

    }

    // TODO - WRITE!
	// saves table to file
    public void saveTable(CacheTable tab){

    }

	// loads table into memory and moves to the top of the cache list
	// removes older tables based on the cacheSize limit
    public void loadTableToMem(CacheTable tab) throws Exception{
    	// load table 
    	//calculate size of table
    	setTableMostRecent(tab);
    	clearOldTables();
    }

	// moves table to the top of the cache list indicating recent use.
	// in the process downgrades recentness of other tables, increasing
	// likelihood they will be removed from memory
    private void setTableMostRecent(CacheTable tab){
    	CacheTable temp=null;
    	for(int i=0; i<cache.size(); i++){
    		if(cache.get(i).getName().equals(tab.getName())){
    			temp= cache.get(i);
    			cache.remove(i);
    			cache.add(0,temp);
    			return;
    		}
    	}
    	if(temp==null){
    		System.out.println("table "+tab.getName()+" not found");
    		throw new Error();
    	}
    }


    // clear out any tables over memory limit, saving to file first
	// moves through tables from most recently used to oldest, summing memory usage
	// once memory usage is exceeded it begins removing tables from memory
	//TODO: make this smarter. Removing table 4 might mean we needn't remove table 5 also.
		// eg. if table 5 is smaller than table 4
    private void clearOldTables() throws Exception{
    	int inMem=0;
    	CacheTable ct;
    	DbFile dbf;
    	for(int i=0; i<cache.size(); i++){
    		ct=cache.get(i);
    		inMem+=ct.getSize();
    		if(inMem>this.cacheSize && ct.getIsInMemory()){
    			dbf = getFile(ct.getName(), false);
				DbTable t = ct.getTable();
				if(t==null){
					throw new Exception("tied to save table "+ct.getName()+" but does not exist");
				}
				dbf.writeToFile(t);
    			ct.setIsInMemory(false);
    			cache.get(i).getTable().truncate();
    		}
    	}
    }

	// gets file corresponding to table name. File name is <tablename>.<extension> where extension is defined in DbFile
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


}