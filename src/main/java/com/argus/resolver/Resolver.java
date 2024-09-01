package com.argus.resolver;

import com.argus.Query;
import com.argus.QueryResult;

/**
 * Base class for all classes that can resolve queries.
 */
public interface Resolver {

    /**
     * Try to fulfil the given query. If the query can be fulfilled return
     * the query result otherwise returns null.
     */
    public QueryResult tryResolve(final Query query);
}
