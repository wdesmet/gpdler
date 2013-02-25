package net.straininfo2.grs.idloader.bioproject.xmlparsing;

import net.straininfo2.grs.idloader.bioproject.bindings.TypePackage;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class DocumentChunkerTest {

    /**
     * Utility function that reads the bioproject XML into binding objects.
     */
    public static List<TypePackage> parseBioProjectFile() throws
            JAXBException, SAXException, ParserConfigurationException, IOException {
        JAXBContext context = JAXBContext.newInstance(TypePackage.class);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        final List<TypePackage> packageList = new LinkedList<>();
        DocumentChunker chunker = new DocumentChunker(context, new PackageProcessor() {
            @Override
            public void processPackage(TypePackage nextPackage) {
                packageList.add(nextPackage);
            }
        });
        reader.setContentHandler(chunker);
        URL xmlUrl = DocumentChunkerTest.class.getClassLoader().getResource("bioproject.xml");
        if (xmlUrl == null) {
            throw new RuntimeException("Unable to fetch bioproject.xml");
        }
        else {
            reader.parse(xmlUrl.toExternalForm());
            return packageList;
        }
    }

    /**
     * Pass a bioproject XML file to chunker, checks if chunking works.
     */
    @Test
    public void parseBioProjectXml() throws Exception {
        assertEquals(4, parseBioProjectFile().size());
    }
}
