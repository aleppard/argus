////////////////////////////////////////////////////////////////////////////////
package com.argus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.stream.Collectors;

/**
 * Encapsulates a user query.
 */
public class Query {

    private Context context;
    private String rawQuery;
    private String normalisedQuery;

    private List<String> wordList;
    private List<String> normalisedWordList;
    
    public Query(final Context context, final String query) {
        this.context = context;
        this.rawQuery = query.trim();

        // Create a normalised version of the string that converts it
        // to lower case, removes leading and trailing whitespace and
        // normalises and collapses the remaining white space.
        this.normalisedQuery =
            this.rawQuery.toLowerCase().trim().replaceAll("\\s+", " ");
        
        this.wordList = buildWordList(this.rawQuery);
        this.normalisedWordList = buildWordList(this.normalisedQuery);
    }

    private static List<String> buildWordList
        (final String queryString) {
        // We need to collapse spaces before splitting to avoid
        // empty elements.
        return Arrays.stream(queryString
                             .trim()
                             .replaceAll("\\s+", " ")
                             .split(" "))
            .collect(Collectors.toList());
    }
    
    public Context getContext() {
        return context;
    }
    
    public String getRawString() {
        return rawQuery;
    }

    public String getNormalisedString() {
        return normalisedQuery;
    }

    public List<String> getWordList() {
        return new ArrayList<>(wordList);
    }

    public List<String> getNormalisedWordList() {
        return new ArrayList<>(normalisedWordList);
    }
}
