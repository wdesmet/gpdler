package net.straininfo2.grs.idloader;

import net.straininfo2.grs.idloader.bioproject.eutils.EutilsDownloader;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class LoaderTest {

    @Test
    @Category(IntegrationTest.class)
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
        l.setDownloader(new EutilsDownloader());
        l.getDownloader().setEmail("blah@somethingelse");
        l.checkEmailWasSet();
    }

}
