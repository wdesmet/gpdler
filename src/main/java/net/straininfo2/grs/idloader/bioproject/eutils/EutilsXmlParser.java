package net.straininfo2.grs.idloader.bioproject.eutils;

import net.straininfo2.grs.idloader.Loader;
import net.straininfo2.grs.idloader.bioproject.domain.mappings.Category;
import net.straininfo2.grs.idloader.bioproject.domain.mappings.Mapping;
import net.straininfo2.grs.idloader.bioproject.domain.mappings.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class EutilsXmlParser {

    private final static Logger logger = LoggerFactory.getLogger(EutilsXmlParser.class);

    private final XMLInputFactory factory;

    private ConcurrentHashMap<Category, Category> categoryMap;

    private ConcurrentHashMap<Provider, Provider> providerMap;

    public EutilsXmlParser() {
        this.categoryMap = new ConcurrentHashMap<Category, Category>();
        this.providerMap = new ConcurrentHashMap<Provider, Provider>();
        this.factory = XMLInputFactory.newFactory();
    }

    public ConcurrentHashMap<Category, Category> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(ConcurrentHashMap<Category,Category> categoryMap) {
        this.categoryMap = categoryMap;
    }

    public ConcurrentHashMap<Provider, Provider> getProviderMap() {
        return providerMap;
    }

    public void setProviderMap(ConcurrentHashMap<Provider, Provider> providerMap) {
        this.providerMap = providerMap;
    }

    public Map<Integer, List<Mapping>> parseMappings(InputStream xml) throws XMLStreamException {
        XMLEventReader stream = factory.createXMLEventReader(xml);
        Map<Integer, List<Mapping>> result = new HashMap<>();
        while (stream.hasNext()) {
            XMLEvent next = stream.nextEvent();
            if (next.isStartElement() && next.asStartElement().getName().getLocalPart().equalsIgnoreCase("IdUrlSet")) {
                // FF to the Id node
                while (!(next.isStartElement() && next.asStartElement().getName().getLocalPart().equalsIgnoreCase("Id")))
                    next = stream.nextEvent();
                Integer id = parseNumber(stream);
                // FF to the mappings
                List<Mapping> mappings = parseMapping(stream);
                result.put(id, mappings);
            }
        }
        return result;
    }

    public List<Mapping> parseMapping(XMLEventReader stream) throws XMLStreamException {
        List<Mapping> mappings = new LinkedList<Mapping>();
        logger.debug("Stream acquired, parsing");
        while (stream.hasNext() &&
                !(stream.peek().isEndElement() &&
                        stream.peek().asEndElement().getName().getLocalPart().equalsIgnoreCase("IdUrlSet"))) {
            XMLEvent next = stream.peek();
            if (next.isStartElement()
                    && next.asStartElement().getName()
                    .getLocalPart()
                    .equalsIgnoreCase("ObjUrl")) {
                logger.debug("Adding an objurl");
                mappings.add(parseObjUrlMapping(stream));
            } else {
                stream.nextEvent();
            }
        }
        return mappings;
    }

    static String extractSingleElement(XMLEventReader stream)
            throws XMLStreamException {
        StringBuilder sb = new StringBuilder();
        while (stream.hasNext() && !(stream.peek().isEndElement())) {
            XMLEvent next = stream.nextEvent();
            if (next.isCharacters()) {
                Characters chars = next.asCharacters();
                if (!chars.isWhiteSpace()) {
                    sb.append(chars.getData());
                }
            }
        }
        logger.trace("Extracted data {} from tag.", sb);
        return sb.toString();
    }

    private static int parseNumber(XMLEventReader stream)
            throws XMLStreamException {
        StringBuilder sb = new StringBuilder(5);
        while (stream.peek().isCharacters()) {
            sb.append(stream.nextEvent().asCharacters().getData());
        }
        if (!stream.nextEvent().isEndElement()) {
            throw new RuntimeException(
                    "Did not end up with end tag while parsing number.");
        }
        return Integer.parseInt(sb.toString());
    }

    private static void clearWhiteSpace(XMLEventReader stream)
            throws XMLStreamException {
        while (stream.hasNext() && stream.peek().isCharacters()
                && stream.peek().asCharacters().isWhiteSpace()) {
            stream.nextEvent();
        }
    }

    private static void parseIds(XMLEventReader stream,
                                 EntrezSearchResult result) throws XMLStreamException {
        logger.debug("Parsing ids");
        clearWhiteSpace(stream);
        while (stream.hasNext()
                && stream.peek().isStartElement()
                && stream.nextEvent().asStartElement().getName().getLocalPart()
                .equals("Id")) {
            logger.trace("Adding an id.");
            result.addId(parseNumber(stream));
            clearWhiteSpace(stream);
        }
        logger.debug("Finished parsing ids");
    }

    public EntrezSearchResult parsePartialIds(InputStream input) throws  XMLStreamException{
        XMLEventReader stream = factory.createXMLEventReader(input);
        return parsePartialIds(stream);
    }

    public EntrezSearchResult parsePartialIds(XMLEventReader stream)
            throws XMLStreamException {
        EntrezSearchResult result = new EntrezSearchResult();
        while (stream.hasNext()) {
            XMLEvent next = stream.nextEvent();
            if (next.isStartElement()) {
                String name = next.asStartElement().getName().getLocalPart();
                if ("Count".equalsIgnoreCase(name)) {
                    result.setCount(parseNumber(stream));
                } else if ("RetMax".equalsIgnoreCase(name)) {
                    result.setRetMax(parseNumber(stream));
                } else if ("RetStart".equalsIgnoreCase(name)) {
                    result.setRetStart(parseNumber(stream));
                } else if ("IdList".equalsIgnoreCase(name)) {
                    parseIds(stream, result);
                }
            }
        }
        return result;
    }

    /**
     * Parse out an ObjUrl mapping from the supplied stream. The stream should
     * be positioned right before the ObjUrl start element.
     *
     *
     * @param stream
     *            xml event stream pointing to an ncbi linkout file right before
     *            ObjUrl
     * @return a single {@link net.straininfo2.grs.idloader.bioproject.domain.mappings.Mapping}, pointing to a URL and a particular
     *         {@link net.straininfo2.grs.idloader.bioproject.domain.mappings.Provider}
     * @throws javax.xml.stream.XMLStreamException
     */
    public Mapping parseObjUrlMapping(XMLEventReader stream)
            throws XMLStreamException {
        String url = null;
        String subjectType = null;
        Category category = null;
        String linkName = null;
        Provider provider = null;
        while (stream.hasNext()
                && !(stream.peek().isEndElement() && stream.peek()
                .asEndElement().getName().getLocalPart()
                .equalsIgnoreCase("ObjUrl"))) {
            XMLEvent next = stream.nextEvent();
            if (next.isStartElement()) {
                StartElement start = next.asStartElement();
                if ("Url".equalsIgnoreCase(start.getName().getLocalPart())) {
                    url = EutilsXmlParser.extractSingleElement(stream);
                } else if ("SubjectType".equalsIgnoreCase(start.getName()
                        .getLocalPart())) {
                    subjectType = EutilsXmlParser.extractSingleElement(stream);
                } else if ("Category".equalsIgnoreCase(start.getName()
                        .getLocalPart())) {
                    category = new Category(EutilsXmlParser.extractSingleElement(stream));
                } else if ("LinkName".equalsIgnoreCase(start.getName()
                        .getLocalPart())) {
                    linkName = EutilsXmlParser.extractSingleElement(stream);
                } else if ("Provider".equalsIgnoreCase(start.getName()
                        .getLocalPart())) {
                    provider = parseProvider(stream);
                }
            }
        }
        return new Mapping(url, subjectType, linkName,
                Loader.dedupKey(categoryMap, category), provider);
    }

    private Provider parseProvider(XMLEventReader stream)
            throws XMLStreamException {
        XMLEvent next = stream.nextEvent();
        String name = null;
        String abbr = null;
        String idString = null;
        String url = null;
        while (!(next.isEndElement() && next.asEndElement().getName()
                .getLocalPart().equalsIgnoreCase("Provider"))) {
            if (next.isStartElement()) {
                StartElement start = next.asStartElement();
                String tagName = start.getName().getLocalPart();
                String content = EutilsXmlParser.extractSingleElement(stream);
                if ("name".equalsIgnoreCase(tagName)) {
                    name = content;
                } else if ("NameAbbr".equalsIgnoreCase(tagName)) {
                    abbr = content;
                } else if ("url".equalsIgnoreCase(tagName)) {
                    url = content;
                } else if ("id".equalsIgnoreCase(tagName)) {
                    idString = content;
                }
            }
            next = stream.nextEvent();
        }

        if (idString == null) {
            throw new RuntimeException("Id not found for provider " + name);
        } else {
            int id = Integer.parseInt(idString);
            return Loader.dedupKey(providerMap, new Provider(name, abbr, id, url));
        }
    }
}
