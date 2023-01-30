package com.argus;

import java.time.Instant;
import java.time.ZoneOffset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Query that converts Unix epoch times to date and time strings. Unix
 * epoch times are the number of seconds since
 * 1970-01-01T00:00:00Z.
 */
public class UnixEpochQuery implements Query
{
    public @Override QueryResult getResult(final Context context,
                                           final String query) {
        // @todo Move this kind of logic into a new Query class so
        // that it's re-usable.
        // @todo Run trim() on each word here rather than below.
        ArrayList<String> words =
            new ArrayList(Arrays.asList(query.toLowerCase().split(" ")));
        Collections.sort(words);
        if (words.size() < 2 || words.size() > 4) return null;

        // The number will be the first.
        final String number = words.get(0);
        words.remove(0);

        // Except a number and any combination or order of these words.
        for (String word : words) {
            word = word.trim();
            
            if (!word.equals("epoch") &&
                !word.equals("unix") &&
                !word.equals("time")) {
                return null;
            }
        }

        try {
            final long unixEpoch = Long.parseLong(number);
            if (unixEpoch < 0) { return null; }

            final Instant instant = Instant.ofEpochSecond(unixEpoch);
            return CurrentTimeQuery.toResult(context,
                                             query,
                                             instant.atOffset(ZoneOffset.UTC));
        }
        catch (NumberFormatException exception) {
            return null;
        }
    }
}
