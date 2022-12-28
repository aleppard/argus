package com.argus;

import java.io.StringReader;

import java.nio.charset.StandardCharsets;

import java.util.Base64;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A query that JWT (Json Web Tokens).
 *
 * Example JWT:
 *
 * eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c 
 */
public class JwtDecoderQuery implements Query
{
    private static final Logger LOGGER =
        Logger.getLogger(JwtDecoderQuery.class.getName());
    
    private Pattern jwtPattern =
        Pattern.compile("([a-zA-Z0-9=_\\-]{8,})\\.([a-zA-Z0-9=_\\-]{8,})\\.[a-zA-Z0-9=_\\-]{8,}");


    /** Convert a Base64 value to a formatted JSON string. */
    private static String base64ToJson(Base64.Decoder decoder, final String base64) {
        final String decoded = 
            new String(decoder.decode(base64), StandardCharsets.UTF_8);
        final JSONObject json = new JSONObject(new JSONTokener(decoded));
        return json.toString(2);
    }
    
    public @Override QueryResult getResult(final Context context,
                                           final String query) {
        
        final Matcher matcher = jwtPattern.matcher(query);
        if (!matcher.matches()) return null;

        try {
            Base64.Decoder decoder = Base64.getUrlDecoder();
            
            final String header = base64ToJson(decoder, matcher.group(1));
            final String payload = base64ToJson(decoder, matcher.group(2));
            
            TableQueryResult result = new TableQueryResult(query);
            result.addRow(new TableQueryResult.Cell("Header"),
                          new TableQueryResult.Cell(header, true));
            result.addRow(new TableQueryResult.Cell("Payload"),
                          new TableQueryResult.Cell(payload, true));            
            
            return result;
        }
        catch (Exception exception) {
            // If there is a problem it's not a JWT.
            return null;
        }
    }
}
