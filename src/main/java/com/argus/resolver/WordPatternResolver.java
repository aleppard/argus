////////////////////////////////////////////////////////////////////////////////
package com.argus.resolver;

import com.argus.Query;
import com.argus.QueryResult;
import com.argus.SingleQueryResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.ClassPathResource;

/**
 * A query that returns a list of words that match a pattern.
 * A pattern should consist of a single group of letters with at
 * least one question mark, e.g. "h?ve" which would match
 * "have", "hive" and "hove".
 *
 * @todo Generate word list dynamically, e.g. from Wikipedia.
 * @todo Returned words should have links to dictionary definitions.
 */
public class WordPatternResolver implements Resolver
{
    private Boolean loaded = false;
    private List<String> words = null;

    private Pattern lettersAndQuestionMarks =
        Pattern.compile("[a-zA-Z\\?]{2,}");

    private List<String> loadWords() {
        words = new ArrayList<>();

        try {
            // @todo Perhaps generate this from the dictionary used by DictionaryResolver.
            InputStream inputStream =
                new ClassPathResource("/word_list.txt").getInputStream();
            InputStreamReader inputStreamReader =
                new InputStreamReader(inputStream);
            BufferedReader bufferedReader =
                new BufferedReader(inputStreamReader);
            while (true) {
                final String line = bufferedReader.readLine();
                if (line == null) break;
                words.add(line.trim());
            }
        }
        catch (IOException exception) {
            // @todo Log.
        }
            
        return words;
    }
    
    /**
     * Get the list of words. Note we load on first access as this
     * query is relatively rare and we don't want to waste memory.
     */
    private List<String> getWords() {
        synchronized(loaded) {
            if (!loaded) {
                loadWords();
                loaded = true;
            }

            return words;
        }
    }

    /**
     * Given a word, pattern, e.g. "h?ve" return if the given word
     * matches that pattern; e.g. in this case "have" would but "home"
     * wouldn't.
     */
    public boolean matches(final String pattern, final String word) {
        if (pattern.length() != word.length()) return false;

        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) == '?') continue;
            if (Character.toLowerCase(pattern.charAt(i)) !=
                Character.toLowerCase(word.charAt(i))) {
                return false;
            }
        }

        return true;
    }
    
    public @Override QueryResult tryResolve(final Query query) {
        final String queryString = query.getRawString();
        
        // Ignore any query that isn't a single word made up of
        // letters and question marks.
        if (!lettersAndQuestionMarks.matcher(queryString).matches()) {
            return null;
        }

        // Ignore any query that doesn't contain a question mark.
        if (!queryString.contains("?")) {
            return null;
        }

        final List<String> words = getWords();

        final List<String> matchingWords = new ArrayList<>();
        for (final String word : words) {
            if (matches(queryString, word)) {
                matchingWords.add(word);
            }
        }

        if (matchingWords.size() == 0) return null;

        return new SingleQueryResult(query,
                                     String.join("\n", matchingWords),
                                     true);
    }
}
