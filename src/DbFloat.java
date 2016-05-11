/**
 * float type for database. See DbType - the interface this class implements
 */
public class DbFloat implements DbType{

    private double val;

    DbFloat(){ }
    DbFloat(int val){
        set(val);
    }
    DbFloat(String val){
        set(val);
    }
    DbFloat(float val){
        set(val);
    }
    DbFloat(double val){
        set(val);
    }
    public Object get(){return val;};
    public int getSize(){return -1;}
    public DbTypeName getType(){return DbTypeName.FLOAT;}

    public String print(){return Double.toString(val);};

    public void   set(Object val){
        if(val instanceof Double ){
            this.val=(double)val;
            return;
        }
        if(val instanceof Float ){
            this.val=(float)val;
            return;
        }

        if(val instanceof Integer){
            this.val=(int)val;
            return;
        }
        if(val instanceof String){
            try {
                this.val = Double.parseDouble((String) val);
                return;
            } catch(NumberFormatException e){
                System.out.println("Invalid input for Int");
                throw e;
            }
        }
        if(val==null){
            this.val = 0.0;
            return;
        }
        System.out.println("Invalid input for Int");
        throw new IllegalArgumentException();
    };


}
