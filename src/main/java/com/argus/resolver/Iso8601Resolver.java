package com.argus.resolver;

import com.argus.Query;
import com.argus.QueryResult;

import java.time.OffsetDateTime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Query that handles decoding ISO 8601 dates and times into a human
 * readable date and times in the user's local time zone, e.g.
 * "2022-12-24T01:08:48Z".
 *
 * @see https://en.wikipedia.org/wiki/ISO_8601
 * @todo Handle decoding more ISO 8601 date/times/periods e.g.
 * R5/2008-03-01T13:00:00Z/P1Y2M10DT2H30M
 * @todo This should work even if the time stap does not have a trailing Z.
 */
public class Iso8601Resolver implements Resolver
{
    private List<DateTimeFormatter> dateFormatters =
        new ArrayList<>();
    
    public Iso8601Resolver() {
        // @todo Add more.
        dateFormatters.add
            (DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
    }

    public @Override QueryResult tryResolve(final Query query) {    
        OffsetDateTime dateTime = null;

        // @todo Run quick check to test if this could be a date.

        // Try different date time formats.
        for (final DateTimeFormatter dateFormatter: dateFormatters) {
            try {
                dateTime = OffsetDateTime.parse(query.getRawString(),
                                                dateFormatter);
                break;
            }
            catch (DateTimeParseException exception) {
            }
        }

        // Parse yyyy-MM-dd'T'HH:mm:ssXXX.
        if (dateTime == null) {
            try {
                dateTime = OffsetDateTime.parse(query.getRawString());
            }
            catch (DateTimeParseException exception) {
            }
        }

        if (dateTime == null) return null;

        return CurrentTimeResolver.toResult(query, dateTime);
    }
}
