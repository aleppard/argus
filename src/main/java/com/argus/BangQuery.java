package com.argus;

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
public class BangQuery
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
    public String getResult(final String query)
        throws UnsupportedEncodingException {        
        if (!query.startsWith("!")) return null;
        final int firstSpace = query.indexOf(' ');
        if (firstSpace == -1) return null;
        
        final String prefix = query.substring(1, firstSpace);
        final Object urlObject = bangQueries.get(prefix);
        if (urlObject == null) return null;

        final String url = (String)urlObject;
        final String postPrefix = query.substring(firstSpace);

        return new RedirectionBuilder(url, postPrefix).build();
    }
}
