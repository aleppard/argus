package com.argus;

import java.net.URLEncoder;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * Query that handles DuckDuckGo style bang questions, e.g.
 *
 * !w half life
 *
 * Would search for the term "half life" in Wikipedia.
 *
 * The supported bang queries are stored in src/main/resources/bangs.yaml.
 *
 * @see https://duckduckgo.com/bang.
 */
public class BangQuery implements Query
{
    private Map<String, Object> bangQueries;

    public BangQuery() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
            .getClassLoader()
            .getResourceAsStream("bangs.yaml");
        bangQueries = yaml.load(inputStream);
    }

    /**
     * Given a bang query return the web page content to return to the user
     * (which will be a redirection to the web server that can run
     * the query directly) or null null if it's not a bang style query.
     */
    public @Override QueryResult getResult(final Context context,
                                           final String query) {
        if (!query.startsWith("!")) return null;
        final int firstSpace = query.indexOf(' ');
        if (firstSpace == -1) return null;
        
        final String prefix = query.substring(1, firstSpace);
        final Object urlObject = bangQueries.get(prefix);
        if (urlObject == null) return null;

        try {
            final String url = (String)urlObject;
            final String postPrefix = query.substring(firstSpace);
            
            return new RedirectionQueryResult
                (url + URLEncoder.encode(postPrefix,
                                         "UTF-8"));
        }
        catch (UnsupportedEncodingException exception) {
            // @todo Log this error.
            return null;
        }
    }
}
