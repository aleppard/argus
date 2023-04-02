package com.argus;

import java.net.URLEncoder;

import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.logging.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * Servlet to run user queries.
 */
@RestController
public class QueryServlet
{
    private static final Logger LOGGER =
        Logger.getLogger(QueryServlet.class.getName());

    private QueryEngine queryEngine = new QueryEngine();

    @GetMapping("/query")    
    public void get(HttpServletRequest request,
                    HttpServletResponse response)
        throws IOException {

        boolean isLocalQueryOnly = false;
        
        // @todo Should we also collapse double spaces here?
        final String queryParameter = request.getParameter("q").trim();
        String queryString = queryParameter;
        
        // Queries prefixed with "! " are only run locally and are not
        // passed on to an external search engine.
        if (queryString.startsWith("! ")) {
            queryString = queryString.substring(1).trim();
            isLocalQueryOnly = true;
        }

        final Context context = new Context(request.getParameter("time_zone"));
        final Query query = new Query(context, queryString);
        QueryResult result = queryEngine.tryResolve(query);

        // If we couldn't process the query locally then redirect
        // to a search engine if the query isn't local only.
        if (result == null) {
            if (isLocalQueryOnly) {
                result = new EmptyQueryResult(query);
            }
            else {
                final String url = 
                    (Settings.getInstance().getDefaultSearchEngineUrl() +
                     URLEncoder.encode(queryParameter, "UTF-8"));
                result = new RedirectionQueryResult(url);
            }
        }
            
        result.setResponse(response);
    }
}
