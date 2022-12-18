package com.argus;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Representation of a non-empty query results.
 */
public interface QueryResult {

    /**
     * Return the result of the query to the caller.
     */
    public void setResponse(HttpServletResponse response) throws IOException;
};
