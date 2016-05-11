/**
 * Created by dan on 28/02/16.
 */
public class TestAll {
    Tester t = new Tester();

    public static void main(String[] args) {

        DbRecordTest.run();
        DbTableTest.run();
        DbFileTest.run();
        DbTypeTests.run();
        DbPrintTest.run();
        DbTest.run();
    }
}
