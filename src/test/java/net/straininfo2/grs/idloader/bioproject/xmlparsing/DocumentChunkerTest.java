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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

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
        final List<TypePackage> packageList = new LinkedList<>();
        DocumentChunker chunker = new DocumentChunker(context, new PackageProcessor() {
            @Override
            public void processPackage(TypePackage nextPackage) {
                packageList.add(nextPackage);
            }
        });
        reader.setContentHandler(chunker);
        reader.parse(this.getClass().getClassLoader().getResource("bioproject.xml").toExternalForm());
        assertEquals(2, packageList.size());
    }
}
