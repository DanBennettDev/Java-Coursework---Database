import java.util.ArrayList;
import java.util.List;

/**
 * Class to Print tables
 */
public class DbPrint {

    // Constants for operation
    private final int PRINT_DEC_PLACES = 3;
    private final int PRINTROWSMAX = 100;
    private final String COLSPACER = " | ";

    public void printTable(DbTable t, int maxRows){
        List<Integer> colWs = getColWidths(t);
        printHeader(t,colWs);
        printBody(t, maxRows, colWs);
    }


    public void printTable(DbTable t){
        printTable(t, PRINTROWSMAX);
    }

    // prints the header row to screen based on list of column names and precalculated column widths
    private void printHeader(DbTable t, List<Integer> widths){
        String name, divider = COLSPACER;
        System.out.print(COLSPACER);

        int tWidth=1, thisWidth = 0;
        for(int i=0; i< widths.size(); i++){
            thisWidth = widths.get(i);
            name = padItem(t.getFieldName(i),thisWidth, DbTypeName.STRING);
            System.out.print( name + COLSPACER);
            while(thisWidth-- > 0){
                divider = divider + '-';
            }
            divider = divider + COLSPACER;
        }
        System.out.print("\n");
        System.out.println(divider);
    }

    // prints the body of a table based on list of column names and precalculated column widths
    private void printBody(DbTable t, int maxRows, List<Integer> widths){
        String val;
        for(int i=0; i< Math.min(t.getRowCount(), maxRows); i++){
            System.out.print(COLSPACER);
            for(int j=0; j< widths.size(); j++){
                val = padItem(valToString(t,i,j),widths.get(j), t.getFieldType(j));
                System.out.print( val + COLSPACER);
            }
            System.out.print("\n");
        }
    }

    // pads out a string with spaces to fit the indicated column width. Positions padding appropriate to datatype
    private String padItem(String val, int colW, DbTypeName type){
        int padCnt = colW - val.length();
        if(padCnt<0){
            System.out.println();
            System.out.println("ERROR: invalid length for field in table");
            throw new IllegalArgumentException();
        }
        String pad = new String(new char[padCnt]).replace("\0", " ");
        if(type == DbTypeName.FLOAT || type == DbTypeName.INT){
            return pad + val;
        }
        return val + pad;
    }

    // converts an incoming value to string, reactive to the data type of the incoming value
    private String valToString(DbTable t, int row, int col){
        switch(t.getFieldType(col)){
            case STRING:
                return (String)t.getValue(row, col);
            case FLOAT:
                return fpointToString((double)t.getValue(row,col));
            case INT:
                return ((Integer)t.getValue(row, col)).toString();
            default:
                System.out.println("Invalid Data Type to valToString Function");
                throw new IllegalArgumentException();
        }
    }

    // converts a floating point to string with number of decimal places defined in PRINT_DEC_PLACES
    private String fpointToString(double in){
        return  String.format("%1."+PRINT_DEC_PLACES+"f", in);
    }

    // calculates column widths for the fields based on length of field name and length of data stored in it
    private List<Integer> getColWidths(DbTable t){
        List<Integer> colWs = new ArrayList<>();
        int labelW;
        for(int i=0; i<t.getFieldCount(); i++){
            labelW = t.getFieldName(i).length();
            colWs.add(getColWidth(t, labelW, i));
        }
        return colWs;
    }

    // calculates column width for a single field,
    // based on width of field name (provided as labelW) and data in the field
    private int getColWidth(DbTable t, int labelW, int colNo){
        switch(t.getFieldType(colNo)){
            case STRING:
                if(labelW > t.getFieldSize(colNo)){
                    return labelW;
                }
                return getMaxStringWidth(t,colNo, labelW);
            case INT:
                return getMaxIntWidth(t,colNo, labelW);
            case FLOAT:
                return getMaxFloatWidth(t,colNo, labelW);
            default:
                throw new IllegalArgumentException();
        }
    }

    // get maximum width of data in a column of strings
    private int getMaxStringWidth(DbTable t, int colNo, int labelW){
        if(t.getFieldType(colNo)!=DbTypeName.STRING){
            System.out.println("non-String field interrogated via getMaxStringWidth()");
            throw new IllegalArgumentException();
        }
        int maxW = labelW, thisW;
        for(int i=0; i<t.getRowCount(); i++){
            thisW= ((String)t.getValue(i, colNo)).length();
            if(thisW>maxW){ maxW=thisW; }
        }
        return maxW;
    }

    // get maximum width of data in a column of integers
    private int getMaxIntWidth(DbTable t, int colNo, int labelW){
        if(t.getFieldType(colNo)!=DbTypeName.INT){
            System.out.println("non-int field interrogated via getMaxIntWidth()");
            throw new IllegalArgumentException();
        }
        int maxW = labelW, thisW;
        for(int i=0; i<t.getRowCount(); i++){
            thisW= getIntWidth((int)t.getValue(i, colNo));
            if(thisW>maxW){ maxW=thisW; }
        }
        return maxW;
    }

    // get maximum width of data in a column of floats
    private int getMaxFloatWidth(DbTable t, int colNo, int labelW){
        if(t.getFieldType(colNo)!=DbTypeName.FLOAT){
            System.out.println("non-float field interrogated via getMaxFloatWidth()");
            throw new IllegalArgumentException();
        }
        int maxW = labelW, thisW, spaceForDPAndMinus;
        for(int i=0; i<t.getRowCount(); i++){
            thisW = Integer.toString((int)(double)t.getValue(i, colNo)).length();
            spaceForDPAndMinus = thisW<0 ? 2:1;
            thisW += (spaceForDPAndMinus + PRINT_DEC_PLACES);
            if(thisW>maxW){ maxW=thisW; }
        }
        return maxW;
    }

    // returns width of a single integer
    private int getIntWidth(int in){
        if(in==Integer.MIN_VALUE){return 11;}
        if(in<0){return getIntWidth(-in)+1;}
        if(in<10){return 1;}
        if(in<100){return 2;}
        if(in<1000){return 3;}
        if(in<10000){return 4;}
        if(in<100000){return 5;}
        if(in<1000000){return 6;}
        if(in<10000000){return 7;}
        if(in<100000000){return 8;}
        if(in<1000000000){return 9;}
        return 10;
    }

    public static void tests() {
        System.out.println("Testing: DbPrint internals");

        Tester t = new Tester();
        Boolean check = false;

        DbPrint print = new DbPrint();

        t.is(print.padItem("12345", 7, DbTypeName.INT), "  12345", "padItem pads correctly when width larger than string");

        check=false;
        try {
            t.is(print.padItem("12345", 3, DbTypeName.STRING), "12345  ", "padItem pads correctly when width larger than string");
        } catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "padItem throws error when col too short for string");


        t.is(print.getIntWidth(1000),4, "getIntWidth correct on 1000");
        t.is(print.getIntWidth(1),1, "getIntWidth correct on 1");
        t.is(print.getIntWidth(-1000),5, "getIntWidth correct on -1000");
        t.is(print.getIntWidth(-99991000),9, "getIntWidth correct on -99991000");
        t.is(print.getIntWidth(1111111111),10, "getIntWidth correct on 1111111111");
        t.is(print.getIntWidth(Integer.MIN_VALUE),11, "getIntWidth correct on MIN_VALUE");


        t.is(print.fpointToString(1.231415), "1.231", "fpointToString test1");
        t.is(print.fpointToString(123456.7), "123456.700", "fpointToString test2");
        t.is(print.fpointToString(.000012345), "0.000", "fpointToString test3");
        t.is(print.fpointToString(-.000012345), "-0.000", "fpointToString test4");


        DbTable tab = new DbTable("animals");
        tab.addField("name", DbTypeName.STRING, 30);
        tab.addField("size", DbTypeName.STRING, 30);
        tab.addField("fierceness", DbTypeName.STRING, 30);
        tab.addField("int", DbTypeName.INT);
        tab.addField("float", DbTypeName.FLOAT);

        tab.insertRecord("giraffe", "large", "moderate", "1", "1.23456" );
        tab.insertRecord("angry gopher", "small", "moderate", "100000", "-12345678.9" );
        tab.insertRecord("calm gopher", "small", "negligible", "-1000001", "-0.000123456" );
        tab.insertRecord("penguin", "smallish", "zero", "123", "9999999" );

        t.is(print.getMaxIntWidth(tab,3,3),8, "getMaxIntWidth works when col max width wider than label");
        t.is(print.getMaxIntWidth(tab,3,9),9, "getMaxIntWidth works when when label wider than col max width");

        t.is(print.getMaxStringWidth(tab,0,4),12, "getMaxStringWidth works when col max width wider than label");
         t.is(print.getMaxStringWidth(tab,0,13),13, "getMaxStringWidth works when when label wider than col max width");

        t.is(print.getColWidth(tab,4,0),12, "getColWidth works when col max width wider than label");
        t.is(print.getColWidth(tab,9,1),9, "getColWidth works when label wider than col max width");

        t.is(print.valToString(tab,3,0),"penguin", "valToString converts String to String");
        t.is(print.valToString(tab,1,3),"100000", "valToString converts int to String");
        t.is(print.valToString(tab,0,4),"1.235", "valToString converts fpoint val to String");

        print.printHeader(tab,print.getColWidths(tab));
        print.printBody(tab, 200, print.getColWidths(tab));
        System.out.println("\n\n");
        print.printBody(tab, 2, print.getColWidths(tab));

        t.results();
    }





}
