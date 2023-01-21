package com.argus;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

/**
 * Returns the current time in a variety of formats.
 *
 * @see https://en.wikipedia.org/wiki/ISO_8601
 */
public class CurrentTimeQuery implements Query
{
    public @Override QueryResult getResult(final Context context,
                                           final String query) {
        final String normalisedQuery = query.toLowerCase().trim();

        // This is where a ML model would help.
        if (!normalisedQuery.equals("now") &&
            !normalisedQuery.equals("time") &&
            !normalisedQuery.equals("the time") &&
            !normalisedQuery.equals("current time") &&
            !normalisedQuery.equals("the current time") &&
            !normalisedQuery.equals("what is the time") &&
            !normalisedQuery.equals("what is the current time") &&
            !normalisedQuery.equals("whats the time") &&
            !normalisedQuery.equals("whats the current time")) {
            return null;
        }

        return toResult(context, query, OffsetDateTime.now());
    }

    /**
     * Return a date time in a variety of formats.
     */
    public static QueryResult toResult(final Context context,
                                       final String query,
                                       OffsetDateTime dateTime) {
        final ZoneId zoneId =
            context.getZoneId() != null? context.getZoneId() :
            ZoneId.systemDefault();
        final ZonedDateTime zonedDateTime =
            dateTime.atZoneSameInstant(zoneId);
        
        final DateTimeFormatter outputFormatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL,
                                                  FormatStyle.FULL);
        final String localTimeString = outputFormatter.format(zonedDateTime);
        final String iso8601String =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.format
            (dateTime.atZoneSameInstant(ZoneOffset.UTC)).toString();
        
        // Number of seconds since 1970-01-01T00:00:00Z.
        final String unixEpochString = Long.toString(dateTime.toEpochSecond());
        
        // @todo Return a title and link to Wikipedia.

        TableQueryResult result = new TableQueryResult(query);
        result.addRow(new TableQueryResult.Cell("Local Time"),
                      new TableQueryResult.Cell(localTimeString));
        result.addRow(new TableQueryResult.Cell("ISO 8601 UTC"),
                      new TableQueryResult.Cell(iso8601String, true));
        result.addRow(new TableQueryResult.Cell("Unix Epoch"),
                      new TableQueryResult.Cell(unixEpochString, true));
        return result;

    }
}
