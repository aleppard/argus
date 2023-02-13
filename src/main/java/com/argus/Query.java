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

    private List<String> normalisedWordList;
    
    public Query(final Context context, final String query) {
        this.context = context;
        this.rawQuery = query.trim();

        // @todo This should also collapse double spaces etc.
        this.normalisedQuery = this.rawQuery.toLowerCase();

        this.normalisedWordList = buildNormalisedWordList(this.normalisedQuery);
    }

    private static List<String> buildNormalisedWordList
        (final String queryString) {
        return new ArrayList(Arrays.stream(queryString.split("\\s"))
                             .map(word -> word.trim())
                             .collect(Collectors.toList()));
    }
    
    // MYTODO DOC these
    public Context getContext() {
        return context;
    }
    
    public String getRawString() {
        return rawQuery;
    }

    public String getNormalisedString() {
        return normalisedQuery;
    }

    public List<String> getNormalisedWordList() {
        return normalisedWordList;
    }
}
