package com.argus.resolver;

import com.argus.Query;
import com.argus.QueryResult;
import com.argus.TableQueryResult;

/**
 * Returns information about a single character or emoji.
 *
 * @todo Support quotes around characters, e.g. ' ', " ".
 * @todo Support control characters such as "\n".
 * @todo Support taking code points/HTML and converting to character, e.g.
 * "U+2714" and "&#10004" should both return this page for the tick
 * character.
 * @todo For letters it should say which letter of the alphabet it is,
 * including for non-English alphabets.
 */
public class CharacterResolver implements Resolver
{
    public @Override QueryResult tryResolve(final Query query) {
        // We don't want to get the normalised string as this would
        // convert letters to lower case.
        final String queryString =
            query.getRawString().trim().replaceAll("\\s+", " ");
        final int codePoints[] = queryString.codePoints().toArray();
        if (codePoints.length != 1) {
            return null;
        }

        final int codePoint = codePoints[0];

        // @todo It would be nice if the character was large and is
        // used as the page title.

        TableQueryResult result = new TableQueryResult(query);
        result.addRow("Character", queryString);

        result.addRow("Name", Character.getName(codePoint));
        Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);
        if (block != null) {
            result.addRow("Block", block.toString());
        }
        
        // @todo Display lower and upper case version of character
        // if possible.
        
        if (codePoint <= 127) {
            // @todo Handle 127 to 255 ASCII characters too.
            result.addRow("ASCII Decimal", Long.toString(codePoint));
            result.addRow("ASCII Hex", "0x" +
                          Integer.toHexString(codePoint).toUpperCase());
        }
        
        result.addRow("Code Point",
                      "U+" + Integer.toHexString(codePoint).toUpperCase());
        result.addRow("HTML", "&#" + codePoint + ";");

        return result;
    }
}
