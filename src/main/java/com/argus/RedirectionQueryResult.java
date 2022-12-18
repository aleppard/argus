package com.argus;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Representation of a query result that redirects the page to another site.
 */
public class RedirectionQueryResult implements QueryResult {

    private String url;
    
    public RedirectionQueryResult(final String url) {
        this.url = url;
    }
    
    @Override public void setResponse(HttpServletResponse response)
        throws IOException {

        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.addHeader("Location", url);
    }
}
