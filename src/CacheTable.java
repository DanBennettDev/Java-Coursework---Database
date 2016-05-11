import javax.print.DocFlavor;

/**
 * Created by dan on 14/03/16.
 */

public class CacheTable{

    // these sizes only take into account the data size, not infrastructure for type
    // or any java overheads
    // ok as a working model, but will need to make more accurate later
    private final int INTSIZE = 4;
    private final int FLOATSIZE = 4;
    private final int CHARSIZE = 2;


    DbTable table;
    int size;
    int rowSize;
    boolean isInMemory;

    CacheTable(String tableName){
        this.table = new DbTable(tableName);
        this.size=0;
        this.rowSize=0;
        this.isInMemory=false;
    }

    public DbTable getTable(){return table;}
    public String getName(){return table.getName();}
    public int getSize(){return size;}
    public int getRowSize(){return rowSize;}
    public boolean getIsInMemory(){return isInMemory;}

    public void setIsInMemory(boolean inMem){
        this.isInMemory = inMem;
    }

    // expects table structure to have been set/ loaded
    public void setRowSize(){
        int cumSize=0;
        for(int i=0; i<this.table.getFieldCount(); i++){
            switch(this.table.getFieldType(i)){
                case STRING:
                    cumSize+= (this.table.getFieldSize(i)*CHARSIZE);
                    break;
                case INT:
                    cumSize+= INTSIZE;
                    break;
                case FLOAT:
                    cumSize+= FLOATSIZE;
                    break;
                default:
                    System.out.println("invalid type in setRowSize()");
                    throw new Error();
            }
        }
        this.rowSize = cumSize;
    }

    public static void test() {

        Tester t = new Tester();
        System.out.println("Testing: DbCache - interface");
        Boolean check = false;

        CacheTable ct = new CacheTable("tablename");

        t.is(ct.getName(),"tablename", "check initialisation and getname" );
        t.is(ct.getIsInMemory(),false, "check initialisation of IsInMemory" );
        t.is(ct.getSize(),0, "check initialisation of Size" );
        t.is(ct.getRowSize(),0, "check initialisation of rowSize" );

        ct.setIsInMemory(true);
        t.is(ct.getIsInMemory(),true, "check setIsInMemory" );

        ct.getTable().addField("string", DbTypeName.STRING, 20);
        ct.getTable().addField("int", DbTypeName.INT, -1);
        ct.getTable().addField("float", DbTypeName.FLOAT, -1);


        ct.setRowSize();

        t.is(ct.getRowSize(),48,"check setRowSize calculates correctly");




        t.results();


    }

}
