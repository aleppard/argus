package com.argus.resolver;

import com.argus.Query;
import com.argus.QueryResult;
import com.argus.SingleQueryResult;

import java.nio.charset.StandardCharsets;

import java.util.Base64;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A query that decodes Base64 sequences.
 *
 * @see https://en.wikipedia.org/wiki/Base64
 * @todo Support "decode xxxx".
 * @todo Handle binary data.
 */
public class Base64DecoderResolver implements Resolver
{
    private Pattern lowerCaseCharacters =
        Pattern.compile("[a-z]");
    private Pattern upperCaseCharacters =
        Pattern.compile("[A-Z]");
    private Pattern numbers = 
        Pattern.compile("[0-9]");
    private Pattern nonBase64Characters =
        Pattern.compile("[^a-zA-Z0-9=_/\\+/\\-]");

    /**
     * Try to decode the given string using the given decoder.
     *
     * @return the decoded string as a query result or null if
     * unable to decode.
     */
    private QueryResult tryDecode(final Query query,
                                  Base64.Decoder decoder) {
        try {
            byte[] result = decoder.decode(query.getRawString());

            // @todo Try other charsets.
            // @todo Support binary data.
            // @todo Use CharsetDecoder to have more controller.
            // Ignore decodings that aren't valid strings. How can we do
            // this?
            return new SingleQueryResult(query,
                                         new String(result,
                                                    StandardCharsets.UTF_8));
        }
        catch (IllegalArgumentException exception) {
            // Not a valid base64 scheme for this encoding.
            return null;
        }
    }
    
    public @Override QueryResult tryResolve(final Query query) {
        final String queryString = query.getRawString();
        
        // Check the string doesn't contain any characters that aren't
        // base64.
        {
            final Matcher matcher = nonBase64Characters.matcher(queryString);
            if (matcher.find()) return null;
        }

        // If the string finishes with a "=" that's a good sign it might
        // be base64 encoded. Otherwise check that the string contains at
        // least one lower case letter, one upper case letter and one number.
        // This allows us not to minimise spurious decoding of non-base64
        // strings.
        //
        // @todo If the use prefixed the query with decode we should
        // skip this check as a base64 string doesn't have to match
        // these rules.
        {
            if (!queryString.endsWith("=")) {
                if (!lowerCaseCharacters.matcher(queryString).find() ||
                    !upperCaseCharacters.matcher(queryString).find() ||
                    !numbers.matcher(queryString).find()) {
                    return null;
                }
            }
        }
        
        QueryResult result = tryDecode(query, Base64.getDecoder());
        if (result == null) {
            result = tryDecode(query, Base64.getMimeDecoder());
        }
        if (result == null) {
            result = tryDecode(query, Base64.getUrlDecoder());
        }

        return result;
    }
}
