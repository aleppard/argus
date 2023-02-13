package com.argus.resolver;

import com.argus.Query;
import com.argus.QueryResult;
import com.argus.SingleQueryResult;
import com.argus.TableQueryResult;

import java.security.SecureRandom;

import java.util.UUID;

/**
 * Query to generate random hex strings if requested.
 *
 * @todo Expand query to generate other randon numbers, strings,
 * phrases etc.
 * @todo Expand query to have finer controls (e.g random 32 bit hex).
 * @todo To be more secure it would be better to generate a random number
 * on the server and then combine it with a random number generated on
 * the client (via Javascript). This way the client would get a more
 * secure random number then just generating on the client alone and
 * the server would not have access to a potential secret.
 */
public class RandomNumberGeneratorResolver implements Resolver
{
    private SecureRandom random = new SecureRandom();
    
    // @todo Support different UUID types.
    private QueryResult tryRandomUuidQuery(final Query query,
                                           final String normalisedQuery) {
        // @todo Implement a more robust, flexible and reusable matching
        // scheme.
        if (!normalisedQuery.equals("random uuid") &&
            !normalisedQuery.equals("uuid random")) { return null; }

        final UUID uuid = UUID.randomUUID();
        return new SingleQueryResult(query, uuid.toString());
    }

    /**
     * Generate a random string of a given length from a set of
     * characters.
     */
    private String generateRandomString(final String characters,
                                        final int length) {
        StringBuffer buffer = new StringBuffer();
        
        for (int i = 0; i < length; i++) {
            final int number = random.nextInt(characters.length());
            buffer.append(characters.charAt(number));
        }

        return buffer.toString();
    }

    private QueryResult tryRandomHexStringQuery(final Query query,
                                                final String normalisedQuery) {
        if (!normalisedQuery.equals("random key") &&
            !normalisedQuery.equals("random hex") &&
            !normalisedQuery.equals("random hash") &&
            !normalisedQuery.equals("hex random") &&
            !normalisedQuery.equals("key random") &&
            !normalisedQuery.equals("hash random")) {
            return null;
        }

        final String HEX_CHARACTERS = "0123456789ABCDEF";
        final int BIT_COUNTS[] = {64, 128, 192, 256};
        final int BITS_PER_CHAR = 4;

        // Maximum 256-bits.
        final String randomHex = generateRandomString(HEX_CHARACTERS, 64);

        TableQueryResult result = new TableQueryResult(query);

        for (final int bitCount : BIT_COUNTS) {
            result.addRow
                (new TableQueryResult.Cell(Long.toString(bitCount) + " bits"),
                 new TableQueryResult.Cell(randomHex.substring
                          (0, bitCount / BITS_PER_CHAR),
                          true));
        }

        return result;
    }

    private QueryResult tryRandomPasswordQuery(final Query query,
                                               final String normalisedQuery) {
        if (!normalisedQuery.equals("random password") &&
            !normalisedQuery.equals("password random")) { return null; }

        TableQueryResult result = new TableQueryResult(query);

        final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";        
        final int PASSWORD_LENGTHS[] = {12, 16, 24};
        
        final int maximumPasswordLength =
            PASSWORD_LENGTHS[PASSWORD_LENGTHS.length - 1];
        
        final String alphaCharacters = ALPHABET + ALPHABET.toUpperCase();
        final String randomAlphaString =
            generateRandomString(alphaCharacters, maximumPasswordLength);
        
        final String alphaNumericCharacters = alphaCharacters + "0123456789";
        final String randomAlphaNumericString =
            generateRandomString(alphaNumericCharacters, maximumPasswordLength);
        
        // Avoid these characters that might cause issues: << ()"\:>>.
        final String alphaNumericSymbolCharacters =
            alphaNumericCharacters + "!@#$%^&*-_=+;',<.>?/|`~";
        final String randomAlphaNumericSymbolString =
            generateRandomString(alphaNumericSymbolCharacters,
                                 maximumPasswordLength);
        
        for (final int passwordLength : PASSWORD_LENGTHS) {
            final String passwords =
                randomAlphaString.substring(0, passwordLength) + "\n" +
                randomAlphaNumericString.substring(0, passwordLength) + "\n" +
                randomAlphaNumericSymbolString.substring(0, passwordLength);
            
            result.addRow
                (new TableQueryResult.Cell(Long.toString(passwordLength) +
                                           " characters"),
                 new TableQueryResult.Cell(passwords, true));
        }

        return result;
    }
    
    public @Override QueryResult tryResolve(final Query query) {
        final String normalisedQuery = query.getNormalisedString();
        
        QueryResult result = tryRandomUuidQuery(query, normalisedQuery);
        if (result == null) {
            result = tryRandomHexStringQuery(query, normalisedQuery);
        }
        if (result == null) {
            result = tryRandomPasswordQuery(query, normalisedQuery);
        }
        
        return result;
    }
}
