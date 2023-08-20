////////////////////////////////////////////////////////////////////////////////
package com.argus.resolver;

import com.argus.Query;
import com.argus.QueryResult;
import com.argus.SingleQueryResult;

/**
 * Returns information about the client's IP address.
 */
public class IpResolver implements Resolver
{
    public @Override QueryResult tryResolve(final Query query) {
        final String queryString = query.getNormalisedString();

        // @todo Also with "address".
        if (!queryString.equals("ip") &&
            !queryString.equals("my ip") &&
            !queryString.equals("whats my ip") &&
            !queryString.equals("what is my ip")) {
            return null;
        }

        return new SingleQueryResult(query,
                                     query.getContext().getClientIp());
    }
}
