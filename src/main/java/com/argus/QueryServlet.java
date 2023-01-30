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

    private QueryEngine queryEngine = new QueryEngine();
    
    @Override public void doGet(HttpServletRequest request,
                                HttpServletResponse response)
        throws IOException {
        
        // @todo Should we also collapse double spaces here?
        final String queryParameter = request.getParameter("q").trim();
        final Context context = new Context(request.getParameter("time_zone"));
        QueryResult result = queryEngine.runQuery(context, queryParameter);

        // If we couldn't process the query locally then redirect
        // to DuckDuckGo.
        if (result == null) {
            // @todo This should be configurable.
            result = new RedirectionQueryResult
                ("https://duckduckgo.com/?q=" +
                 URLEncoder.encode(queryParameter, "UTF-8"));
        }
            
        result.setResponse(response);
    }

    @Override public void destroy() {
        queryEngine.close();
        queryEngine = null;
    }
}
