import java.util.Locale;

/**
 * String type for database
 * See DbType - the interface this class implements
 */
public class DbString implements DbType{


    private String val;
    private int size;

    DbString(int size ){
        this.size=size;
    }
    DbString(int size, int val){
        this.size=size;
        set(val);
    }
    DbString(int size, String val){
        this.size=size;
        set(val);
    }
    DbString(int size, float val){
        this.size=size;
        set(val);
    }
    DbString(int size, double val){
        this.size=size;
        set(val);
    }

    public DbTypeName getType(){return DbTypeName.STRING;}
    public Object get(){return val;};
    public int getSize(){return size;}
    public String print(){return val;};

    public void   set(Object val){
        if(val instanceof Integer){
            String temp =Integer.toString((int)val);
            checkTooBig(temp.length());
            this.val=temp;
            return;
        }
        if(val instanceof Double){
            handleFP((double)val);
            return;
        }
        if(val instanceof Float){
            handleFP((double)(float)val);
            return;
        }

        if(val instanceof String){
            String str = (String)val;
            if(str.length()<=this.size){
                this.val = str;
                return;
            };
            this.val=((String)val).substring(0,this.size);
            return;
        }
        if(val==null){
            this.val = "";
            return;
        }
        System.out.println("invalid data type for String field " + val);
        throw new IllegalArgumentException();
    };

    // checks if an integer is too big to be stored in a string of length defined by object
    private void checkTooBig(int in){
        if(in>this.size){
            System.out.println("String too long");
            throw new IllegalArgumentException();
        }
    }

    // converts floating point number (double / float) to string
    // no longer used
//    private String fpToString(double in){
//        String intPart =Integer.toString((int)in);
//        int decPlaces = size - intPart.length();
//        if(decPlaces < 0){
//            System.out.println("number too large to hold in this field");
//            throw new IllegalArgumentException();
//        }
//        if(decPlaces ==0 || decPlaces==1){
//            return intPart;
//        }
//
//        return String.format(Locale.UK, "%."+ decPlaces +"f", in);
//    }

    // handles storage of floating point numbers as string,
    // tries to store in most accurate manner possible given constraints of the field size relative to the
    // incoming number. Truncates to int if cannot fit integer part plus at least 1 decimal place
    // otherwise stores as many decimal places as it can.
    private void handleFP(double in){
        Integer intpart = (int)(in);
        int lenInt = intpart<0 ? 1 : 0;
        lenInt += Integer.toString(intpart).length();

        if(lenInt<=this.size){
            if(lenInt>=this.size-1){
                this.val = String.format("%1.0f", in);
                return;
            }
            int dp = size-lenInt-1;
            String fString = String.format("%1."+dp+"f", in);
            this.val =fString.substring(0,Math.min(this.size, fString.length()));
            return;
        }
        System.out.println("cannot fit value " + in +" in string size " +size );
        throw new IllegalArgumentException();

    }

}
