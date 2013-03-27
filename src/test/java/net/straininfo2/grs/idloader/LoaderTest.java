package net.straininfo2.grs.idloader;

import net.straininfo2.grs.idloader.bioproject.eutils.EutilsDownloader;
import net.straininfo2.grs.idloader.bioproject.xmlparsing.DocumentChunker;
import net.straininfo2.grs.idloader.bioproject.xmlparsing.DocumentChunkerTest;
import net.straininfo2.grs.idloader.bioproject.xmlparsing.DomainConverter;
import net.straininfo2.grs.idloader.bioproject.xmlparsing.PackageProcessor;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoaderTest {

    @Test
    @Category(IntegrationTest.class)
    public void testConstruction() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Loader l = ctx.getBean(Loader.class);
        ctx.close();
        removeTemporaryDatabase();
    }

    @Test
    @Category(IntegrationTest.class)
    public void loadIntoDb() throws ParserConfigurationException, IOException, SAXException, JAXBException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Loader l = ctx.getBean(Loader.class);
        PackageProcessor dbloader = ctx.getBean(DomainConverter.class);
        DocumentChunker.parseXmlFile(DocumentChunkerTest.getBioProjectUrl(), dbloader);
        ctx.close();
        removeTemporaryDatabase();
    }

    public static void removeTemporaryDatabase() {
        try {
            Files.delete(Paths.get("grsdb2.h2.db"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
