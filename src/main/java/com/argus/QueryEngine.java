package com.argus;

import com.argus.resolver.*;

/**
 * Engine to resolve queries.
 */
public class QueryEngine implements AutoCloseable {
    // @todo Come up with a more pluggable resolver mechanism.
    private Resolver[] resolvers =
        new Resolver[]{new BangResolver(),
                    new CharacterResolver(),
                    new ColourResolver(),
                    new CurrentTimeResolver(),
                    new UnixEpochResolver(),
                    new Iso8601Resolver(),
                    new RandomNumberGeneratorResolver(),
                    new JwtDecoderResolver(),
                    new Base64DecoderResolver(),
                    new WordPatternResolver(),
                    new MathResolver(),
                    new UnicodeResolver()};

    public QueryResult tryResolve(final Query query) {
        // @todo Consider trying all queries, not just until we get the
        // first match, as there may be multiple results. We might also want
        // to include a "confidence" value so that we can ignore or deprioritise
        // low value confidence results if another result has a higher
        // confidence.
        // @todo Run queries concurrently.
        QueryResult queryResult = null;

        for (final Resolver resolver : resolvers) {
            queryResult = resolver.tryResolve(query);
            if (queryResult != null) {
                break;
            }
        }

        return queryResult;
    }

    @Override public void close() {
        // @todo Hook this up to MathQuery.
    }
}
