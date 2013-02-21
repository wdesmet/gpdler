package net.straininfo2.grs.idloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TargetIdExtractor {

    private final int start;

    static final Logger logger = LoggerFactory
            .getLogger(TargetIdExtractor.class);

    private static final Map<Integer, TargetIdExtractor> registeredExtractors =
        new HashMap<Integer, TargetIdExtractor>();

    public TargetIdExtractor(int start) {
        this.start = start;
    }

    /**
     * Extracts any values from 'start' up until the end or the first ampersand
     *
     * @param url A URL pointing to the resource at the matching provider site
     * @return the target id
     */
    public String extractTargetId(String url) {
        if (start == -1) {
            return null; // return nothing if we don't know where to start
        }
        try {
            String piece = new URL(url).getFile().substring(start);
            int ampLocation = piece.indexOf('&');
            if (ampLocation != -1) {
                return piece.substring(0, ampLocation);
            } else {
                return piece;
            }
        } catch (MalformedURLException e) {
            logger.warn("Malformed url for extractTargetId: {}", url);
            return null;
        }
    }

    /**
     * Fetches target ID extractor associated with the listed provider,
     * or null if none was found.
     *
     * @return a target ID extractor that works on URLs for given provider, or null
     */
    public static TargetIdExtractor getExtractor(Provider provider) {
        return registeredExtractors.get(provider.getId());
    }

    /**
     * Registers an extractor for a particular ID.
     */
    public static void registerExtractor(int id, TargetIdExtractor extractor) {
        assert(id > 0);
        registeredExtractors.put(id, extractor);
    }
}
