package com.argus;

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
public class Iso8601Query implements Query
{
    private List<DateTimeFormatter> dateFormatters =
        new ArrayList<>();
    
    public Iso8601Query() {
        // @todo Add more.
        dateFormatters.add
            (DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
    }
    
    public @Override QueryResult getResult(final Context context,
                                           final String query) {
        OffsetDateTime dateTime = null;

        // @todo Run quick check to test if this could be a date.

        // Try different date time formats.
        for (final DateTimeFormatter dateFormatter: dateFormatters) {
            try {
                dateTime = OffsetDateTime.parse(query, dateFormatter);
                break;
            }
            catch (DateTimeParseException exception) {
            }
        }

        // Parse yyyy-MM-dd'T'HH:mm:ssXXX.
        if (dateTime == null) {
            try {
                dateTime = OffsetDateTime.parse(query);
            }
            catch (DateTimeParseException exception) {
            }
        }

        if (dateTime == null) return null;

        return CurrentTimeQuery.toResult(context, query, dateTime);
    }
}
