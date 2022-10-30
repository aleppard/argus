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

    // @todo Create query abstraction to support pluggable queries.
    private BangQuery bangQuery = new BangQuery();
    
    @Override public void doGet(HttpServletRequest request,
                                HttpServletResponse response)
        throws IOException {

        final String queryParameter = request.getParameter("q");

        String result = bangQuery.getResult(queryParameter);
        if (result == null) {
            result = 
                new RedirectionBuilder
                ("https://duckduckgo.com/?q=", queryParameter)
                .build();
        }
            
        response.getWriter().println(result);
    }
}
