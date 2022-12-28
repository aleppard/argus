package com.argus;

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
public class Base64DecoderQuery implements Query
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
    private QueryResult tryDecode(Base64.Decoder decoder,
                                  final String query) {
        try {
            byte[] result = decoder.decode(query);

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
    
    public @Override QueryResult getResult(final Context context,
                                           final String query) {
        // Check the string doesn't contain any characters that aren't
        // base64.
        {
            final Matcher matcher = nonBase64Characters.matcher(query);
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
            if (!query.endsWith("=")) {
                if (!lowerCaseCharacters.matcher(query).find() ||
                    !upperCaseCharacters.matcher(query).find() ||
                    !numbers.matcher(query).find()) {
                    return null;
                }
            }
        }
        
        QueryResult result = tryDecode(Base64.getDecoder(), query);
        if (result == null) {
            result = tryDecode(Base64.getMimeDecoder(), query);
        }
        if (result == null) {
            result = tryDecode(Base64.getUrlDecoder(), query);
        }

        return result;
    }
}
