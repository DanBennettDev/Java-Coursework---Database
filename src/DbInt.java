/**
 * int type for database
 * See DbType - the interface this class implements
 */
public class DbInt implements DbType{

    private int val;

    DbInt(){ }
    DbInt(int val){
        set(val);
    }
    DbInt(String val){
        set(val);
    }
    DbInt(float val){
        set(val);
    }
    DbInt(double val){
        set(val);
    }
    public Object get(){return val;};
    public int getSize(){return -1;}
    public DbTypeName getType(){return DbTypeName.INT;}


    public String print(){return Integer.toString(val);};


    public void   set(Object val){
        if(val instanceof Integer ){
            this.val=(int)val;
            return;
        }
        if(val instanceof Float ){
            this.val=(int)(float)val;
            return;
        }
        if(val instanceof Double ){
            this.val=(int)(double)val;
            return;
        }

        if(val instanceof String){
            try {
                String str = (String) val;
                if(str.contains(".")){
                    String[] parts = str.split("\\.");
                    str = parts[0];
                }
                this.val = Integer.parseInt(str);
                return;
            } catch(NumberFormatException e){
                System.out.println("Invalid input for Int");
                throw e;
            }
        }
        if(val==null){
            this.val = 0;
            return;
        }
        System.out.println("Invalid input for Int");
        throw new IllegalArgumentException();
    };


}
