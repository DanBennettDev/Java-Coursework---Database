import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for DbRecord Class
 */
public class DbRecordTest {


    public static void run(){
        Tester t = new Tester();
        Boolean check = false;
        System.out.println("Testing: Records");

        List<DbField> dbfs = new ArrayList<>();
        DbField d;
        d=new DbField("String0", DbTypeName.STRING, 10);
        dbfs.add(d);
        d=new DbField("Int1", DbTypeName.INT);
        dbfs.add(d);
        d=new DbField("Float2", DbTypeName.FLOAT);
        dbfs.add(d);


        DbRecord r = new DbRecord(0,dbfs);
        t.is(r.size(), 3, "construct without values");

        String[] mpt = new String[0];


        check = false;
        try {
            r= new DbRecord(0, dbfs, mpt);
        } catch( Exception e){
            check=true;
        }
        t.is(check,true, "Initialize with insufficient strings throws error");

        r= new DbRecord(0,dbfs, "one", "2", "3.0");
        t.is(r.size(),3, "Construct from populated list of Strings");
        t.is(r.getVal(1), 2, "Construct from populated list gives correct value to index");

        r.set(0,"zero");
        t.is(r.getVal(0), "zero", "set value works");
        check = false;
        try {
            r.set(8, "too far");
        } catch( Exception e){
            check=true;
        }
        t.is(check,true, "Exception on set out of bounds");

        char DELIMITER = 0x0241E;
        char ENDRECORD = 0x0000A;

        r= new DbRecord(0,dbfs, "one"+DELIMITER + "two", "2", "3.0");
        t.is(r.getVal(0), "onetwo", "DELIMITER stripping works on constructor");

        r= new DbRecord(0,dbfs, "one"+ENDRECORD + "two", "2", "3.0");
        t.is(r.getVal(0), "onetwo", "ENDRECORD stripping works on constructor");

        r.set(0,""+ENDRECORD);
        t.is(r.getVal(0), "", "ENDRECORD stripping works on set");

        r.set(0,"ONE"+DELIMITER+"TWO");
        t.is(r.getVal(0), "ONETWO", "DELIMITER stripping works on set");


        t.results();
    }

    public static void main(String[] args) {
        run();
    }
}
