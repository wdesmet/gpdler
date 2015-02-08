package net.straininfo2.grs.idloader.bioproject.eutils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.straininfo2.grs.idloader.TokenBucket;
import net.straininfo2.grs.idloader.bioproject.domain.mappings.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EutilsDownloader {

    public final static String EUTILS_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/";

    /**
     * Number of ids queried per request (as controlled by retMax parameter).
     *
     * If this is lower than the nr of ids in the database, multiple requests
     * will be made to get the full list. At the time of writing, 20k was enough
     * to get everything in one request. The code to get stuff in multiple
     * requests was tested as well and Should Work(TM).
     */
    public final static int NUM_PER_REQUEST = 20000;

    /**
     * Number of mappings to attempt to download per request. Somewhat restricted
     * by url size.
     */
    public final static int MAPPINGS_PER_REQUEST = 50;

    private static final int MAX_ERRORS = 3;  /* maximum nr of errors per item hit */

    /**
     * Email address passed to the eutils as identification. Eutils work without it,
     * but it allows eutils admins to contact you in case of problems (say you
     * accidentally ddos them by running 400 copies of this code on a cluster).
     */
    private String email;

    // timeouts (in ms), set to null if you want infinity
    private Integer connectTimeout;

    private Integer readTimeout;

    private EutilsXmlParser xmlParser;

    public final static Logger logger = LoggerFactory.getLogger(EutilsDownloader.class);

    public List<Integer> downLoadIds(WebResource source)
            throws UniformInterfaceException, XMLStreamException {
        // this will fail with a run time exception should something go wrong
        // As that is a fatal exception, we let it kill the program.
        List<Integer> ids = new ArrayList<>();
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

    /**
     * Downloads a set of LinkOut mappings using eutils. This expects the web resource
     * parameter to be configured to use the eutils llinks command with appropiate
     * parameters for the requested database. Note that supplying too many identifiers
     * might result in error.
     *
     * @param source web resource pointing to an appropiate eutils URL
     * @param ids a list of object identifiers
     * @return InputStream for the returned XML from eutils
     */
    InputStream downloadMapping(WebResource source, List<Integer> ids) {
        InputStream xml = null;
        String input = StringUtils.collectionToCommaDelimitedString(ids);
        logger.info("Downloading mapping for {} ids: {}.", ids.size(), input);
        for (int errors = 0; errors < MAX_ERRORS; errors++) {
            try {
                xml = source.queryParam("id", input).get(
                        InputStream.class);
                break; /* only stay in loop if an error occurs */
            } catch (ClientHandlerException e) { // problem while reading the file (like timeout)
                logger.warn("Exception while downloading: {}", e.getCause().getMessage());
                logger.debug("Error count: {}/{}", errors + 1, MAX_ERRORS);
                try {
                    Thread.sleep(20); // short backoff before trying again
                } catch (InterruptedException ie) {}
            } catch (UniformInterfaceException ue) { // HTTP return code >300
                logger.warn("Could not fetch mappings because of HTTP {}", ue.getResponse().getStatus());
                logger.debug("Error count: {}/{}", errors + 1, MAX_ERRORS);
            }
        }
        return xml;
    }

    public Map<Integer, List<Mapping>> downloadAllMappings() throws XMLStreamException {
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
        return this.createMapping(ids, link);
    }

    public Map<Integer, List<Mapping>> createMapping(List<Integer> ids,
                                                     WebResource source) throws XMLStreamException {
        Map<Integer, List<Mapping>> result = new HashMap<>();
        logger.info("Creating mapping from source {}", source);
        TokenBucket barrier = new TokenBucket(3); // 3 requests per second
        int count = 0;
        long starttime = System.currentTimeMillis();
        try {
            for (int i = 0; i < ids.size(); i += MAPPINGS_PER_REQUEST) {
                int toIndex = i + MAPPINGS_PER_REQUEST;
                List<Integer> mappingIds = ids.subList(i, toIndex < ids.size() ? toIndex : ids.size());
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
                InputStream xml = downloadMapping(source, mappingIds);
                if (xml != null) {
                    Map<Integer, List<Mapping>> mappings = xmlParser.parseMappings(xml);
                    result.putAll(mappings);
                } else {
                    logger.warn(
                            "Could not download information for project IDs {}-{}!",
                            i, toIndex);
                }
            }
        } finally {
            barrier.stop();
        }
        logger.debug("Returning mapping");
        return result;
    }

    // utility functions

    public static void addInts(List<Integer> target, int[] source) {
        for (int i : source) {
            target.add(i);
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

    // getters/setters


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public EutilsXmlParser getXmlParser() {
        return xmlParser;
    }

    public void setXmlParser(EutilsXmlParser xmlParser) {
        this.xmlParser = xmlParser;
    }

}
