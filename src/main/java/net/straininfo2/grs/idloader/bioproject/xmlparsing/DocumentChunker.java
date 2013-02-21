package net.straininfo2.grs.idloader.bioproject.xmlparsing;

import net.straininfo2.grs.idloader.bioproject.bindings.PackageSet;
import net.straininfo2.grs.idloader.bioproject.bindings.TypePackage;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.sql.rowset.spi.XmlReader;
import javax.xml.bind.*;
import javax.xml.parsers.SAXParserFactory;
import java.util.Enumeration;
import java.util.List;

/**
 * Takes a bioproject XML and helps to parse it in Package sized chunks.
 * The idea is to make a new unmarshaller for each package.
 *
 * Based on the sample from JAXB distribution.
 */
public class DocumentChunker extends XMLFilterImpl {

    private JAXBContext context;

    /* copied reference to locator to pass to unmarshaller, is in superclass */
    private Locator locator;

    /* Need to keep track of namespaces, to pass them to the unmarshaller */
    private NamespaceSupport namespaceSupport = new NamespaceSupport();

    /* Actual unmarshaller, starts empty */
    private UnmarshallerHandler handler = null;

    /* packageset namespace, qName and attributes, so we can pass it to the unmarshaller virtually */
    private String packageSetUri;
    private String packageSetQName;
    private Attributes packageSetAttributes;

    public DocumentChunker (JAXBContext context) {
        this.context = context;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        namespaceSupport.pushContext();
        namespaceSupport.declarePrefix(prefix, uri);
        super.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        namespaceSupport.popContext();
        super.endPrefixMapping(prefix);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);
        this.locator = locator;
    }

    private void addNameSpacesTo(UnmarshallerHandler handler) throws SAXException {
        @SuppressWarnings("unchecked")
        Enumeration<String> e = namespaceSupport.getPrefixes();
        while (e.hasMoreElements()) {
            String prefix = e.nextElement();
            handler.startPrefixMapping(prefix, namespaceSupport.getURI(prefix));
        }
        if (namespaceSupport.getURI("") != null) {
            handler.startPrefixMapping("", namespaceSupport.getURI(""));
        }
    }

    private void removeNamespacesFrom(UnmarshallerHandler handler) throws SAXException {
        @SuppressWarnings("unchecked")
        Enumeration<String> e = namespaceSupport.getPrefixes();
        while (e.hasMoreElements()) {
            String prefix = e.nextElement();
            handler.endPrefixMapping(prefix);
        }
        if (namespaceSupport.getURI("") != null) {
            handler.endPrefixMapping("");
        }
    }

    @Override
    public void startElement(String uri, String name, String qName, Attributes attributes) throws SAXException {

        if (name.equals("PackageSet")) {
            this.packageSetUri = uri;
            this.packageSetQName = qName;
            this.packageSetAttributes = attributes;
        }
        if (handler == null && name.equals("Package")) {
            try {
                Unmarshaller unmarshaller = context.createUnmarshaller();
                this.handler = unmarshaller.getUnmarshallerHandler();

                // start a new document virtually
                handler.startDocument();
                handler.setDocumentLocator(locator);
                // also pass it a PackageSet
                handler.startElement(packageSetUri, "PackageSet", packageSetQName, attributes);

                // start sending events to this handler
                this.setContentHandler(handler);
                addNameSpacesTo(handler);
            } catch (JAXBException e) {
                throw new SAXException("Could not acquire an unmarshaller, unrecoverable", e);
            }
        }
        super.startElement(uri, name, qName, attributes);
    }

    @Override
    public void endElement(String uri, String name, String qName) throws SAXException {
        super.endElement(uri, name, qName);

        // now if we just passed end of package, unmarshall it and throw away the unmarshaller
        if (name.equals("Package")) {
            if (handler == null) {
                throw new RuntimeException("Found end of package section, but no handler was set.");
            }
            // handler != null, end the document for handler, and get cracking on the result
            removeNamespacesFrom(handler);
            handler.endElement(packageSetUri, "PackageSet", packageSetQName);
            handler.endDocument();
            setContentHandler(new DefaultHandler());
            try {
                handleObject(((PackageSet) handler.getResult()).getPackages());
            } catch (JAXBException e) {
                throw new SAXException("Unmarshalling the package at end failed, at line " +
                        locator.getLineNumber(), e);
            } finally {
                this.handler = null;
            }
        }
    }

    /* Temporary, should push this to another class */
    public void handleObject(List<TypePackage> nPackage) {
        System.out.println(nPackage.get(0).getProject().getProject().getProjectDescr().getName());
    }

}
