package com.argus;

/**
 * Base class for all classes that can fufill queries.
 *
 * @todo What is a better name? This class does not represent the query
 * per-se but machinery that can fufill some queries.
 */
public interface Query {

    /**
     * Try to fufill the given query. If the query can be fufilled return
     * the query result otherwise returns null.
     */
    public QueryResult getResult(final String query);
}
