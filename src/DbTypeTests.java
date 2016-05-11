/**
 * Tests for DbString Class
 */
public class DbTypeTests {

    public static void run() {
        Tester t = new Tester();
        System.out.println("Testing: DbString");
        Boolean check = false;

        DbType str = new DbString(5);

        str.set("abcd");
        t.is(str.get(), "abcd", "set and get string len=size-1 works");
        str.set("abcde");
        t.is(str.get(), "abcde", "set and get string len=size works");
        str.set("abcdefghij");
        t.is(str.get(), "abcde", "set and get string len>size works - truncates");
        str.set(12345);
        t.is(str.get(), "12345", "set and get int len=size works");

        check=false;
        try {
            str.set(1234567);
        } catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "set int len>size throws error");


        str = new DbString(6);
        str.set(1.234);
        t.is(str.get(), "1.2340", "set and get float len=size works");
        str.set(1.23456);
        t.is(str.get(), "1.2346", "set and get float len>size, intpart <size works");
        str.set(12345.6);
        t.is(str.get(), "12346", "set and get float len>size, intpart =size works");
        str.set(123456.7);
        t.is(str.get(), "123457", "set and get float len>size, intpart =size works");

        check=false;
        try {
            str.set(1234567.8);
        } catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "set float with int part len>size throws error");

        str = new DbString(7);
        str.set(1.234567);
        t.is(str.get(), "1.23457", "formatting when size >= 6 - mantissa < 6");
        str.set(1234567.0123);
        t.is(str.get(), "1234567", "formatting when size >= 6 - mantissa > 6");
        str.set(0.0000001234567);
        t.is(str.get(), "0.00000", "formatting when size >= 6 - neg exponent");
        str.set(-0.0000001234567);
        t.is(str.get(), "-0.0000", "formatting when size >= 6 - negative number neg exponent");
        str.set(0.0000001234567);
        t.is(str.get(), "0.00000", "formatting when size >= 6 - negative number pos exponent");

        t.results();


        //INT
        System.out.println("Testing: DbInt");
        t = new Tester();
        DbType i = new DbInt();
        t.is(i.get(), 0, "constructor with no values yields 0");

        i = new DbInt(5);
        t.is(i.get(), 5, "constructor with int yields int");

        i = new DbInt(7.5);
        t.is(i.get(), 7, "constructor with double yields truncated int");

        i = new DbInt(7.5f);
        t.is(i.get(), 7, "constructor with float yields truncated int");

        i = new DbInt("5");
        t.is(i.get(), 5, "constructor with string(int) yields int");

        i = new DbInt("5.5");
        t.is(i.get(), 5, "constructor with string(float) yields truncated int");


        check=false;
        try {
            i = new DbInt("x.5");
        } catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "invalid string throws exception");

        i.set(5);
        t.is(i.get(), 5, "set  with int yields int");

        i.set(7.5);
        t.is(i.get(), 7, "set with double yields truncated int");

        i.set(7.5f);
        t.is(i.get(), 7, "set with float yields truncated int");

        i.set("5");
        t.is(i.get(), 5, "set with string(int) yields int");

        i.set("5.6");
        t.is(i.get(), 5, "set with string(float) yields truncated int");

        check=false;
        try {
            i.set("x.5");
        } catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "set invalid string throws exception");

        t.results();



        //Float
        System.out.println("Testing: DbInt");
        t = new Tester();
        DbType f = new DbFloat();
        t.is(f.get(), 0.0, "constructor with no values yields 0");

        f = new DbFloat(5);
        t.is(f.get(), 5.0, "constructor with int yields correct value");

        f = new DbFloat(5.333333333333);
        t.is(f.get(), 5.333333333333, "constructor with double yields correct value");

        f = new DbFloat(5.5f);
        t.is(f.get(), 5.5, "constructor with float yields correct value");

        f = new DbFloat("5.5");
        t.is(f.get(), 5.5, "constructor with string yields correct value");

        check=false;
        try {
            f = new DbFloat("c.5");
        } catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "invalid string throws exception");


        f.set(5);
        t.is(f.get(), 5.0, "constructor with int yields correct value");

        f.set(5.333333333333);
        t.is(f.get(), 5.333333333333, "constructor with double yields correct value");

        f.set(5.5f);
        t.is(f.get(), 5.5, "constructor with float yields correct value");

        f.set("5.5");
        t.is(f.get(), 5.5, "constructor with string yields correct value");

        check=false;
        try {
            f.set("c.5");
        } catch(IllegalArgumentException e){
            check=true;
        }
        t.is(check, true, "invalid string throws exception");


        t.results();


    }


    public static void main(String[] args) {
        run();
    }
}
