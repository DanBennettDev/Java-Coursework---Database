import java.io.File;
import java.io.IOException;

/**
 * Tests for DbFile Class
 */
public class DbCacheTest {

    public static void run() {

        Tester t = new Tester();
        System.out.println("Testing: CacheTable - internal");
        CacheTable.test();


        System.out.println("Testing: DbCache - interface");
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

        DbCache dbc = new DbCache(128, "testDB");

        dbc.addTable("animals");




        t.results();




    }


    public static void main(String[] args) {
        run();
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
}
