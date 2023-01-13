package com.argus;

import java.net.URLEncoder;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * Query that handles DuckDuckGo style bang questions, e.g.
 *
 * !w half life
 *
 * And also Firefox at (@) queries, e.g.
 *
 * @amazon wuthering heights
 *
 * Would search for the term "half life" in Wikipedia.
 *
 * The supported bang queries are stored in src/main/resources/bangs.yaml.
 *
 * @see https://duckduckgo.com/bang.
 */
public class BangQuery implements Query
{
    private Map<String, String> bangQueries = new HashMap<>();

    public BangQuery() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
            .getClassLoader()
            .getResourceAsStream("bangs.yaml");

        // Load list of bang queries, e.g. x, y, z: URL
        Map<String, Object> bangListQueries = yaml.load(inputStream);

        for (Map.Entry<String, Object> entry : bangListQueries.entrySet()) {
            // Add each bang alternate, e.g. gh, git, github.
            final String[] bangs = entry.getKey().split(",");
            for (String bang : bangs) {
                final String url = (String)entry.getValue();
                bangQueries.put(bang.trim(), url.trim());
            }
        }
    }

    /**
     * Given a bang query return the web page content to return to the user
     * (which will be a redirection to the web server that can run
     * the query directly) or null null if it's not a bang style query.
     */
    public @Override QueryResult getResult(final Context context,
                                           final String query) {
        if (!query.startsWith("!") && !query.startsWith("@")) return null;
        final int firstSpace = query.indexOf(' ');
        if (firstSpace == -1) return null;
        
        final String prefix = query.substring(1, firstSpace);
        final String url = bangQueries.get(prefix);
        if (url == null) return null;

        try {
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
