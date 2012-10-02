package net.straininfo2.grs.idloader;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.straininfo2.grs.idloader.db.MappingDbLoader;
import net.straininfo2.grs.idloader.db.ProjectInfoLoader;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
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

    public final static String EUTILS_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/";

    public final static String GENOMEPRJ_FTP_URL = "ftp://ftp.ncbi.nih.gov/genomes/genomeprj/";

    private final static Logger logger = LoggerFactory.getLogger(Loader.class);

    /**
     * Number of ids queried per request (as controlled by retMax parameter).
     *
     * If this is lower than the nr of ids in the database, multiple requests
     * will be made to get the full list. At the time of writing, 20k was enough
     * to get everything in one request. The code to get stuff in multiple
     * requests was tested as well and Should Work(TM).
     */
    public final static int NUM_PER_REQUEST = 20000;

    private static final int MAX_ERRORS = 3;  /* maximum nr of errors per item hit */

    // timeouts (in ms), set to null if you want infinity
    private Integer connectTimeout;

    private Integer readTimeout;

    private MappingDbLoader dbLoader;

    private ProjectInfoLoader projLoader;

    private EutilsXmlParser xmlParser;

    /**
     * Email address passed to the eutils as identification. Eutils work without it,
     * but it allows eutils admins to contact you in case of problems (say you
     * accidentally ddos them by running 400 copies of this code on a cluster).
     */
    private String email;

    public Loader() {
    }

    /* getters and setters */

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setMappingDbLoader(MappingDbLoader dbLoader) {
        this.dbLoader = dbLoader;
    }

    public void setProjectInfoLoader(ProjectInfoLoader projLoader) {
        this.projLoader = projLoader;
    }

    public void setXmlParser(EutilsXmlParser xmlParser) {
        this.xmlParser = xmlParser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static void main(String[] args) throws XMLStreamException,
            FactoryConfigurationError {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(
                "applicationContext.xml");
        Loader loader = ctx.getBean(Loader.class);
        loader.checkEmailWasSet();
        try {
            loader.downloadAndLoadData();
        } catch (Exception e) {
            logger.error("Exception thrown but not caught during loading, exiting", e);
            System.exit(1);
        }
        logger.debug("Finished loading, main thread exiting");
    }

    public void downloadAndLoadData() throws XMLStreamException {
        loadProjectInformation();
        loadUrls();
    }

    public void loadUrls() throws XMLStreamException, FactoryConfigurationError {
        dbLoader.configureTables();
        Client client = Client.create();
        client.addFilter(new GZIPContentEncodingFilter());
        // set timeouts, the NCBI servers tend to do a lot of that
        client.setReadTimeout(getReadTimeout());
        client.setConnectTimeout(getConnectTimeout());
        WebResource search = createEutilsSearchResource(client);
        // add info to be used by NCBI should there be a problem
        search = search.queryParam("tool", "grs_loader").queryParam("email",
                email);
        List<Integer> ids = this.downLoadIds(search);
        logger.debug("Downloaded {} ids", ids.size());
        WebResource link = createEutilsLinkResource(client);
        link = link.queryParam("tool", "grs_loader").queryParam("email",
                email);
        Map<Integer, List<Mapping>> grs = this.createMapping(ids, link);
        Map<Provider, TargetIdExtractor> extractors = constructExtractors(grs);
        dbLoader.updateIfChanged(grs, extractors);
    }

    void checkEmailWasSet() {
        Properties p = new Properties();
        try {
            p.load(this.getClass().getClassLoader().getResourceAsStream("grsloader.default.properties"));
            String defaultEmail = null;
            if ((defaultEmail = p.getProperty("grs.email")) != null) {
                if (email.equalsIgnoreCase(defaultEmail)) {
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

    WebResource createEutilsLinkResource(Client client) {
        return client.resource(EUTILS_URL + "elink.fcgi").
                queryParam("dbfrom", "bioproject").
                queryParam("cmd", "llinks");
    }

    WebResource createEutilsSearchResource(Client client) {
        return client.resource(EUTILS_URL + "esearch.fcgi");
    }

    public void loadProjectInformation() {
        try {
            projLoader.configureTables();
            logger.debug("Fetching project information files from FTP");
            FTPClient client = new FTPClient();
            try {
                URI uri = new URI(GENOMEPRJ_FTP_URL);
                client.connect(uri.getHost());
                client.setFileType(FTPClient.BINARY_FILE_TYPE);
                client.enterLocalPassiveMode();
                logger.debug("Connecting to {}, returned {}", uri.getHost(),
                        client.getReplyCode());
                client.login("anonymous", email);
                logger.debug("Logging in, returned {}", client.getReplyCode());
                logger.debug("Changing working directory to {}", uri.getPath());
                client.changeWorkingDirectory(uri.getPath());
                logger.debug("CWD returned {}", client.getReplyString());
                parseFtpFiles(client);
            } finally {
                client.logout();
                client.disconnect();
            }
        } catch (URISyntaxException e) {
            logger.error("Malformed URI supplied {}", GENOMEPRJ_FTP_URL);
            e.printStackTrace();
        } catch (IOException io) {
            logger.error("Problem connecting to FTP server");
            io.printStackTrace();
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
        Map<Provider, String> foundUrlList = new HashMap<Provider, String>();
        Map<Provider, TargetIdExtractor> extractors = new HashMap<Provider, TargetIdExtractor>();
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

    public Map<Integer, List<Mapping>> createMapping(List<Integer> ids,
                                                     WebResource source) throws XMLStreamException {
        Map<Integer, List<Mapping>> result = new HashMap<Integer, List<Mapping>>();
        logger.info("Creating mapping from source {}", source);
        TokenBucket barrier = new TokenBucket(3); // 3 requests per second
        int count = 0;
        long starttime = System.currentTimeMillis();
        try {
            for (Integer i : ids) {
                try {
                    logger.debug("Waiting for barrier");
                    barrier.take();
                    count++;
                    if (count % 10 == 0) {
                        logger.debug("Average rate of requests: "
                                + count
                                / ((System.currentTimeMillis() - starttime) / 1000.0));
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(
                            "Unexpected interruption in barrier", e);
                }
                InputStream xml = downloadMapping(source, i);
                if (xml != null) {
                    List<Mapping> mappings = xmlParser.parseMapping(xml);
                    result.put(i, mappings);
                } else {
                    logger.warn(
                            "Could not download information for project ID {}!",
                            i);
                }
            }
        } finally {
            barrier.stop();
        }
        logger.debug("Returning mapping");
        return result;
    }

    /**
     * Downloads a LinkOut mapping using eutils. This expects the web resource
     * parameter to be configured to use the eutils llinks command with appropiate
     * parameters for the requested database.
     *
     * @param source web resource pointing to an appropiate eutils URL
     * @param i identifier of the requested elemented ("id=" in the eutils url)
     * @return InputStream for the returned XML from eutils
     */
    InputStream downloadMapping(WebResource source, Integer i) {
        logger.info("Downloading mapping for id {}.", i);
        InputStream xml = null;
        for (int errors = 0; errors < MAX_ERRORS; errors++) {
            try {
                xml = source.queryParam("id", i.toString()).get(
                        InputStream.class);
                break; /* only stay in loop if an error occurs */
            } catch (ClientHandlerException e) {
                logger.warn("Exception while downloading: {}", e.getCause().getMessage());
                logger.debug("Error count: {}/{}", errors + 1, MAX_ERRORS);
            }
        }
        return xml;
    }

    public List<Integer> downLoadIds(WebResource source)
            throws UniformInterfaceException, XMLStreamException {
        // this will fail with a run time exception should something go wrong
        // As that is a fatal exception, we let it kill the program.
        List<Integer> ids = new ArrayList<Integer>();
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("db", "bioproject");
        params.add("term", "all[filter]");
        params.add("retmax", Integer.toString(NUM_PER_REQUEST));
        logger.debug("Querying esearch using params {}.", params);
        EntrezSearchResult curResult = xmlParser.parsePartialIds(source
                .queryParams(params).get(InputStream.class));
        addInts(ids, curResult.getIds());
        int curStart = NUM_PER_REQUEST;
        while (curResult.getRetStart() + curResult.getIds().length < curResult
                .getCount()) {
            params.putSingle("retstart", Integer.toString(curStart));
            logger.debug("Querying using parameters: {}", params);
            curResult = xmlParser.parsePartialIds(source.queryParams(params)
                    .get(InputStream.class));
            addInts(ids, curResult.getIds());
            curStart = curStart + NUM_PER_REQUEST;
        }
        return ids;
    }

    public static void addInts(List<Integer> target, int[] source) {
        for (int i : source) {
            target.add(i);
        }
    }

    private void parseFtpFiles(FTPClient client) {
        try {
            // stop checking date for now since it doesn't seem to work
            Date lastUpdate = null;// loader.getLatestProkaryoteUpdate();
            if (lastUpdate == null
                    || client.listFiles("lproks_0.txt")[0].getTimestamp()
                    .after(lastUpdate)) {

                InputStream stream = client.retrieveFileStream("lproks_0.txt");
                projLoader.updateProkaryotesMain(stream);
                client.completePendingCommand();
            }

            if (lastUpdate == null
                    || client.listFiles("lproks_1.txt")[0].getTimestamp()
                    .after(lastUpdate)) {
                InputStream stream = client.retrieveFileStream("lproks_1.txt");
                projLoader.updateProkaryotesCompleted(stream);
                client.completePendingCommand();
            }

            if (lastUpdate == null
                    || client.listFiles("lproks_2.txt")[0].getTimestamp()
                    .after(lastUpdate)) {
                InputStream stream = client.retrieveFileStream("lproks_2.txt");
                projLoader.updateProkaryotesInProgress(stream);
                client.completePendingCommand();
            }
            //lastUpdate = projLoader.getLatestEukaryoteUpdate();
            if (lastUpdate == null || client.listFiles("leuks.txt")[0].getTimestamp().after(lastUpdate)) {
                InputStream stream = client.retrieveFileStream("leuks.txt");
                projLoader.updateEukaryotes(stream);
                client.completePendingCommand();
            }

            //lastUpdate = projLoader.getLatestEnvironmentalUpdate();
            if (lastUpdate == null || client.listFiles("lenvs.txt")[0].getTimestamp().after(lastUpdate)) {
                InputStream stream = client.retrieveFileStream("lenvs.txt");
                projLoader.updateEnvironmentals(stream);
                client.completePendingCommand();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <E> E dedupKey(ConcurrentHashMap<E, E> map, E key) {
        E tmp = map.putIfAbsent(key, key);
        return tmp == null ? key : tmp;
    }
}
