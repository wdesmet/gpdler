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

public class DocumentChunkerTest {

    /**
     * Pass a bioproject XML file to chunker, checks if chunking works.
     */
    @Test
    public void parseBioProjectXml() throws JAXBException, SAXException, ParserConfigurationException, IOException {
        JAXBContext context = JAXBContext.newInstance(TypePackage.class);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        DocumentChunker chunker = new DocumentChunker(context);
        System.out.println(getClass().getClassLoader().getResource("bioproject.xml"));
        reader.setContentHandler(chunker);
        reader.parse(this.getClass().getClassLoader().getResource("bioproject.xml").toExternalForm());
    }
}
