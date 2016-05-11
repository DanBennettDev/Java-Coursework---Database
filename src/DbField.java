/**
 * Field class for database - holds information on the field, not data itself
 * functions are self explanatory. Class is barely more than a C style struct
 */
 class DbField {

    private final int DEFAULTSIZE = 127;

    public String name;
    public DbTypeName type;
    public int size;

    DbField(String name, DbTypeName type, int size){
        this.name = name;
        this.type = type;
        if(size<=0){
            size=DEFAULTSIZE;
        }
        if(type == DbTypeName.STRING){
            this.size = size;
        } else {
            this.size = -1;
        }
    }

    DbField(String name, DbTypeName type){
        this(name,type, 0);
    }

    public DbTypeName getType(){return type;};
    public int getSize(){return size;};
    public String getName(){return name;};
    public void setName(String name){
        this.name = name;
    };

}