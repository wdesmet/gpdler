package net.straininfo2.grs.idloader;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class LoaderTest {

    @Test
    public void generalAddIntsTest() {
        // added because I did it entirely wrong, an array with contents
        // [3] would cause IndexOutOfBoundsException
        int[] ar = { 3 };
        List<Integer> l = new LinkedList<Integer>();
        Loader.addInts(l, ar);
        assertTrue("List contains wrong or wrong amount of integers", l.size() == 1 && l.get(0) == ar[0]);
    }

    @Test
    public void addIntsFromEmptyArray() {
        int [] ar = {};
        List<Integer> l = new LinkedList<Integer>();
        Loader.addInts(l, ar);
        assertTrue("List should be empty", l.isEmpty());
    }

    @Test
    public void testConstruction() {
        Loader l = new ClassPathXmlApplicationContext("classpath:applicationContext.xml").getBean(Loader.class);
    }

    @Test(expected = RuntimeException.class)
    public void badEmail() {
        Loader l = new Loader();
        l.checkEmailWasSet();
    }

    @Test
    public void goodEmail() {
        Loader l = new Loader();
        l.setEmail("blah@somethingelse");
        l.checkEmailWasSet();
    }

}
