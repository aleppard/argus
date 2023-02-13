package com.argus.resolver;

import com.argus.Query;
import com.argus.QueryResult;

/**
 * Base class for all classes that can resolve queries.
 */
public interface Resolver {

    /**
     * Try to fufill the given query. If the query can be fufilled return
     * the query result otherwise returns null.
     */
    public QueryResult tryResolve(final Query query);
}
