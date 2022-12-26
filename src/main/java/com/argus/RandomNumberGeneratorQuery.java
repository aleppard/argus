package com.argus;

import java.security.SecureRandom;

import java.util.UUID;

/**
 * Query to generate random hex strings if requested.
 *
 * @todo Expand query to generate other randon numbers, strings,
 * phrases etc.
 * @todo Expand query to have finer controls (e.g random 32bit hex).
 * @todo To be more secure it would be better to generate a random number
 * on the server and then combine it with a random number generated on
 * the client (via Javascript). This way the client would get a more
 * secure random number then just generating on the client alone and
 * the server would not have access to a potential secret.
 */
public class RandomNumberGeneratorQuery implements Query
{
    private SecureRandom random = new SecureRandom();
    
    // @todo Support different UUID types.
    private QueryResult checkRandomUuidQuery(final String originalQuery,
                                             final String normalisedQuery) {
        // @todo Implement a more robust, flexible and reusable matching
        // scheme.
        if (!normalisedQuery.equals("random uuid") &&
            !normalisedQuery.equals("uuid random")) { return null; }

        final UUID uuid = UUID.randomUUID();
        return new SingleQueryResult(originalQuery, uuid.toString());
    }

    // @todo Support different lenghts (e.g. specify number of bits or bytes).
    private QueryResult checkRandomHexStringQuery(final String originalQuery,
                                                  final String normalisedQuery) {
        if (!normalisedQuery.equals("random key") &&
            !normalisedQuery.equals("random hex") &&
            !normalisedQuery.equals("random hash") &&
            !normalisedQuery.equals("hex random") &&
            !normalisedQuery.equals("key random") &&
            !normalisedQuery.equals("hash random")) {
            return null;
        }

        // @todo There are better ways of doing this!
        final String HEX_CHARACTERS = "0123456789ABCDEF";
        StringBuffer randomHex = new StringBuffer();

        // 256 bits
        for (int i = 0; i < 64; i++) {
            int number = random.nextInt(HEX_CHARACTERS.length());
            randomHex.append(HEX_CHARACTERS.charAt(number));
        }
        
        return new SingleQueryResult(originalQuery, randomHex.toString());
        
    }
    
    public @Override QueryResult getResult(final Context context,
                                           final String query) {
        final String normalisedQuery = query.toLowerCase();
        
        QueryResult result = checkRandomUuidQuery(query, normalisedQuery);
        if (result == null) {
            result = checkRandomHexStringQuery(query, normalisedQuery);
        }

        return result;
    }
}
