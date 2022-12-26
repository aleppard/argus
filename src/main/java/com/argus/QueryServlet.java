package com.argus;

import java.net.URLEncoder;

import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.logging.Logger;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet to run user queries.
 */
public class QueryServlet extends HttpServlet
{
    private static final Logger LOGGER =
        Logger.getLogger(QueryServlet.class.getName());

    // @todo Cache these between requests.
    // @todo Come up with a more pluggable query mechanism.
    private Query[] queries =
        new Query[]{new BangQuery(),
                    new Iso8601Query(),
                    new RandomNumberGeneratorQuery()};
    
    @Override public void doGet(HttpServletRequest request,
                                HttpServletResponse response)
        throws IOException {

        // @todo Should we also collapse double spaces here?
        final String queryParameter = request.getParameter("q").trim();
        final Context context = new Context(request.getParameter("time_zone"));
        
        // @todo Consider trying all queries as the same input might
        // triggger multiple results. We might also want to include
        // a "confidence" value so that we can ignore or deprioritise
        // low value confidence results if another result has a higher
        // confidence.
        QueryResult result = null;
        for (final Query query : queries) {
            result = query.getResult(context, queryParameter);
            if (result != null) {
                break;
            }
        }

        // If we couldn't process the query locally then redirect
        // to DuckDuckGo.
        if (result == null) {
            result = new RedirectionQueryResult
                ("https://duckduckgo.com/?q=" +
                 URLEncoder.encode(queryParameter, "UTF-8"));
        }
            
        result.setResponse(response);
    }
}
