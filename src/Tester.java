import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tester.java
 * facilities for unit testing
 * Author: DTB
 * Date: 09/02/16
 */
public class Tester {
    private List<Test> tests = new ArrayList<>();
    private int count=0;

    private class Test{
        private String description;
        private Object got;
        private Object expected;
        private Boolean pass;

        Test(String description, Object got, Object expected, Boolean pass){
            this.description = description;
            this.got = got;
            this.expected = expected;
            this.pass = pass;
        }
        Test(String description, Object got, Object expected){
            this(description, got, expected, true);
        }

    }


    // Test whether two objects or primitive values such as ints are equal.
    public void is(Object got, Object expected, String name, String description, Boolean prints) {
        Test t = new Test(description, got, expected);
        this.tests.add(t);
        count++;
        if (got == expected) return;
        if (got != null && got.equals(expected)) return;

        t.pass = false;
        this.tests.set(count-1,t);
        if(prints) {
            System.out.println("Test " + description + " failed: " + got + ", " + expected);
        }
    }
    public void is(Object got, Object expected, String name){
        is(got, expected, name, name, false);
    }

    public void results() {
        int pass=0, fail=0;
        Test t;
        for(int i=0; i<count; i++){
            t = this.tests.get(i);
            if(t.pass){
                pass++;
            } else {
                fail++;
                System.out.println("Test \"" + t.description + "\" failed: got " + t.got + ", expected: " + t.expected);
            }
        }

        System.out.println("Tests passed: " + pass);
        System.out.println("Tests failed: " + fail);
        System.out.println("\n");

    }


}
