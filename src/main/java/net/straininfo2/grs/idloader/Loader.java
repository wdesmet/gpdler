package net.straininfo2.grs.idloader;

import net.straininfo2.grs.idloader.bioproject.domain.mappings.Mapping;
import net.straininfo2.grs.idloader.bioproject.domain.mappings.Provider;
import net.straininfo2.grs.idloader.bioproject.eutils.EutilsDownloader;
import net.straininfo2.grs.idloader.bioproject.eutils.MappingHandler;
import net.straininfo2.grs.idloader.bioproject.xmlparsing.DocumentChunker;
import net.straininfo2.grs.idloader.bioproject.xmlparsing.DomainConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Downloads genome project IDs from entrez and retrieves the forward mapping
 * for all of them. This is used to build StrainInfo's reverse mapping from
 * various possible project IDs (for instance, straininfo culture IDs) back to
 * the genome project ID from the NCBI.
 *
 * @author wdesmet
 *
 */
public class Loader {

    public final static String GENOMEPRJ_FTP_URL = "ftp://ftp.ncbi.nlm.nih.gov/bioproject/";

    private final static Logger logger = LoggerFactory.getLogger(Loader.class);

    private EutilsDownloader downloader;

    private DomainConverter domainConverter;

    private MappingHandler mappingHandler;

    public Loader() {
    }

    public EutilsDownloader getDownloader() {
        return downloader;
    }

    public void setDownloader(EutilsDownloader downloader) {
        this.downloader = downloader;
    }

    public DomainConverter getDomainConverter() {
        return domainConverter;
    }

    public void setDomainConverter(DomainConverter domainConverter) {
        this.domainConverter = domainConverter;
    }

    public MappingHandler getMappingHandler() {
        return mappingHandler;
    }

    public void setMappingHandler(MappingHandler mappingHandler) {
        this.mappingHandler = mappingHandler;
    }

    public static void main(String[] args) throws XMLStreamException,
            FactoryConfigurationError {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(
                "applicationContext.xml");
        Loader loader = ctx.getBean(Loader.class);
        loader.checkEmailWasSet();
        try {
            loader.loadProjectInformation();
            loader.loadMappings();
        } catch (Exception e) {
            logger.error("Exception thrown but not caught during loading, exiting", e);
            System.exit(1);
        }
        logger.debug("Finished loading, main thread exiting");
    }

    public void loadMappings() throws XMLStreamException, FactoryConfigurationError {
        Map<Integer, List<Mapping>> grs = downloader.downloadAllMappings();
        Map<Provider, TargetIdExtractor> extractors = constructExtractors(grs);
        for (Map.Entry<Integer, List<Mapping>> mappingList : grs.entrySet()) {
            mappingHandler.handleMappings(mappingList.getKey().longValue(), mappingList.getValue(), extractors);
        }
    }

    @Transactional
    public void loadProjectInformation() {
        try {
            URL uri = new URL(new URL(GENOMEPRJ_FTP_URL), "bioproject.xml");
            DocumentChunker.parseXmlFile(uri, domainConverter);
        } catch (MalformedURLException e) {
            logger.error("Malformed URI supplied {}", e);
        } catch (ParserConfigurationException | SAXException | JAXBException e) {
            logger.error("Could not start XML parser {}", e);
        } catch (IOException e) {
            logger.error("Could not connect to Bioproject FTP site {}", e);
        }
    }

    protected void checkEmailWasSet() {
        Properties p = new Properties();
        try {
            p.load(this.getClass().getClassLoader().getResourceAsStream("grsloader.default.properties"));
            String defaultEmail;
            if ((defaultEmail = p.getProperty("grs.email")) != null) {
                if (downloader.getEmail().equalsIgnoreCase(defaultEmail)) {
                    logger.debug("Set email is equal to default {}", defaultEmail);
                    throw new RuntimeException("Error: you need to set an email address (-Dgrs.email=...)");
                }
            }
            else {
                throw new RuntimeException("Error: property grs.email is not defined.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not find default properties file.", e);
        }
    }

    private static int determineFirstDifferingChar(String firstPath, String secondPath) {
        int i = 0;
        int start = -1;
        /* Find the part of the URL where characters start differing */
        while (i < firstPath.length()
                && i < secondPath.length()
                && firstPath.charAt(i) == secondPath
                .charAt(i)) {
            if (firstPath.charAt(i) == '=' || firstPath.charAt(i) == '/') {
                start = i + 1;
            }
            i++;
        }
        /* If start != -1 it's pointing to an '=' or '/' just after this difference. 
         * We don't allow a '/' or '=' inside the extracted part though, so we must
         * skip those if any.
         */
        for (int j = start + 1; j < firstPath.length() && j < secondPath.length(); j++) {
            char firstChar = firstPath.charAt(j);
            char secondChar = secondPath.charAt(j);
            if (firstChar == '&' || secondChar == '&') {
                break;
            }
            if (firstChar == '/' || firstChar == '=' ||
                    secondChar == '/' || secondChar == '=') {
                start = j + 1;
            }
        }
        if (i == firstPath.length()
                || i == secondPath.length()) {
            return start;
        } else {
            return start == -1 ? i : start;
        }
    }

    /**
     * Uses a simple heuristic to construct an instance that can extract target
     * IDs from URLs.
     *
     * The heuristic simply looks at two URLs from the same provider and
     * extracts the parts that differ.
     */
    static Map<Provider, TargetIdExtractor> constructExtractors(
            Map<Integer, List<Mapping>> grs) {
        /*
           * We need to find at least two URLs of each type to compare them. All
           * we do is find the first location in which they differ.
           */
        Map<Provider, String> foundUrlList = new HashMap<>();
        Map<Provider, TargetIdExtractor> extractors = new HashMap<>();
        // used when we can't find a good differentiating point
        TargetIdExtractor dummy = new TargetIdExtractor(-1);
        for (List<Mapping> mappings : grs.values()) {
            for (Mapping mapping : mappings) {
                if (!extractors.containsKey(mapping.getProvider())) {
                    if (!foundUrlList.containsKey(mapping.getProvider())) {
                        foundUrlList.put(mapping.getProvider(),
                                mapping.getUrl());
                    } else {
                        String foundUrl = foundUrlList.remove(mapping
                                .getProvider());
                        try {
                            String firstPath = new URL(foundUrl).getFile();
                            String secondPath = new URL(mapping.getUrl())
                                    .getFile();
                            // only look at the non-host parts of the urls
                            int start = determineFirstDifferingChar(firstPath, secondPath);
                            if (start == -1) {
                                extractors.put(mapping.getProvider(),
                                        dummy);
                            }
                            else {
                                extractors.put(mapping.getProvider(),
                                        new TargetIdExtractor(start));
                            }
                        } catch (MalformedURLException e) {
                            logger.warn("Malformed URL for provider: {}",
                                    mapping.getProvider().getAbbr());
                            logger.warn("Url1: {}", foundUrl);
                            logger.warn("Url2: {}", mapping.getUrl());
                            extractors.put(mapping.getProvider(),
                                    dummy);
                        }
                    }
                }
            }
        }
        for (Provider provider : foundUrlList.keySet()) {
            // add dummy providers for those where we only found one url
            extractors.put(provider, dummy);
        }
        return extractors;
    }

    public static <E> E dedupKey(ConcurrentHashMap<E, E> map, E key) {
        E tmp = map.putIfAbsent(key, key);
        return tmp == null ? key : tmp;
    }
}
