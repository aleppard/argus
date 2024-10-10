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
 * least one question mark or asterix, e.g. "h?ve" which would match
 * "have", "hive" and "hove"; "hav*" would match "have", "having" and
 * others too!).
 *
 * @todo Generate word list dynamically, e.g. from Wikipedia.
 * @todo Returned words should have links to dictionary definitions.
 * @todo It would be good to be able to sort these words alphabetically or by
 * length.
 */
public class WordPatternResolver implements Resolver
{
    private Boolean loaded = false;
    private List<String> words = null;

    private Pattern lettersAndQuestionMarks =
        Pattern.compile("[a-zA-Z\\?\\*]{2,}");

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
     * Convert the input query string into a regular expression.
     */
    private Pattern generateRegularExpression(final String queryString) {
        String patternString = "";
        for (int i = 0; i < queryString.length(); i++) {
            final char character = queryString.charAt(i);
            
            if (character == '?') {
                patternString += '.';
            }
            else if (character == '*') {
                patternString += ".*";
            }
            else {
                patternString += Character.toLowerCase(character);
            }
        }
        
        return Pattern.compile(patternString);
    }
    
    private List<String> findMatchingWords(final String queryString,
                                           final List<String> words) {
        List<String> matchingWords = new ArrayList<>();
        Pattern regularExpression = generateRegularExpression(queryString);

        // @todo Do this functionally.
        for (final String word : words) {
            if (regularExpression.matcher(word.toLowerCase()).matches()) {
                matchingWords.add(word);
            }
        }

        return matchingWords;
    }
    
    public @Override QueryResult tryResolve(final Query query) {
        final String queryString = query.getRawString();
        
        // Ignore any query that isn't a single word made up of
        // letters and question marks.
        if (!lettersAndQuestionMarks.matcher(queryString).matches()) {
            return null;
        }

        // Ignore any query that doesn't contain a question mark or asterix.
        if (!queryString.contains("?") &&
            !queryString.contains("*")) {
            return null;
        }

        // Having two consective asterixes doesn't make a lot of sense,
        // so maybe this isn't a word pattern request.
        if (queryString.contains("**")) {
            return null;
        }
        
        final List<String> words = getWords();
        final List<String> matchingWords = findMatchingWords(queryString, words);
        if (matchingWords.size() == 0) return null;

        return new SingleQueryResult(query,
                                     String.join("\n", matchingWords),
                                     true);
    }
}
