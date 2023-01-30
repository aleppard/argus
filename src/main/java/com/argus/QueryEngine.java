package com.argus;

/**
 * Engine to resolve queries.
 */
public class QueryEngine implements AutoCloseable {
    // @todo Come up with a more pluggable query mechanism.
    private Query[] queries =
        new Query[]{new BangQuery(),
                    new ColourQuery(),
                    new CurrentTimeQuery(),
                    new Iso8601Query(),
                    new RandomNumberGeneratorQuery(),
                    new JwtDecoderQuery(),
                    new Base64DecoderQuery(),
                    new WordPatternQuery(),
                    new MathQuery()};

    public QueryResult runQuery(final Context context,
                                final String queryString) {
        // @todo Consider trying all queries, not just until we get the
        // first match, as there may be multiple results. We might also want
        // to include a "confidence" value so that we can ignore or deprioritise
        // low value confidence results if another result has a higher
        // confidence.
        // @todo Run queries concurrently.
        QueryResult queryResult = null;

        for (final Query query : queries) {
            queryResult = query.getResult(context, queryString);
            if (queryResult != null) {
                break;
            }
        }

        return queryResult;
    }

    @Override public void close() {
    }
}
