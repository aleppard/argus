package com.argus.resolver;

import com.argus.Query;
import com.argus.QueryResult;
import com.argus.TableQueryResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.logging.Logger;

/**
 * Returns lists of unicode characters matching the given name.
 *
 * @todo When searching for characters such as "a" it would be
 * better only to match "A" and not any word with the letter A.
 * Should we apply this elsewhere, e.g. "face" matches "surface integral"
 * which is probably not what was wanted.
 */
public class UnicodeResolver implements Resolver
{
    private static final Logger LOGGER =
        Logger.getLogger(UnicodeResolver.class.getName());

    private class UnicodeCharacter {
        public int codePoint;
        public String name;

        public UnicodeCharacter(int codePoint) {
            this.codePoint = codePoint;
            this.name = Character.getName(codePoint);
        }
    }
    
    private List<UnicodeCharacter> unicodeCharacters =
        new ArrayList<>();

    private final static String MAX_CODE_POINT = "10FFFF";

    private int maxCodePoint;
    
    public UnicodeResolver() {
        maxCodePoint = Integer.parseInt(MAX_CODE_POINT, 16);

        for (int codePoint = 0; codePoint <= maxCodePoint; codePoint++) {
            if (Character.isDefined(codePoint)) {
                unicodeCharacters.add(new UnicodeCharacter(codePoint));
            }
        }
    }
    
    public @Override QueryResult tryResolve(final Query query) {

        // @todo Move this to Query.
        // @todo Run trim() over all words.
        List<String> words = 
            new ArrayList<>(Arrays.asList(query.getRawString()
                                          .toUpperCase().split(" ")));
        if (words.size() < 2) return null;

        // Only match queries with the first or last word being unicode.
        if (words.get(0).equals("UNICODE")) {
            words.remove(0);
        }
        else if (words.get(words.size() - 1).equals("UNICODE")) {
            words.remove(words.size() - 1);
        }
        else {
            return null;
        }

        TableQueryResult result = new TableQueryResult(query);

        for (final UnicodeCharacter character : unicodeCharacters) {
            boolean match = true;
            
            for (final String word : words) {
                if (!character.name.contains(word)) {
                    match = false;
                    break;
                }
            }

            if (!match) continue;
            
            result.addRow
                (new TableQueryResult.Cell("&#" + character.codePoint + ";",
                                           false, false),
                 new TableQueryResult.Cell(character.name),
                 new TableQueryResult.Cell
                 ("U+" + Integer.toHexString(character.codePoint)
                  .toUpperCase()));
        }
        
        return result.getRowCount() > 0? result : null;
    }
}
