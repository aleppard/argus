package com.argus;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

/**
 * Query that handles decoding ISO 8601 dates and times into a human
 * readable date and times in the user's local time zone, e.g.
 * "2022-12-24T01:08:48Z".
 *
 * @see https://en.wikipedia.org/wiki/ISO_8601
 * @todo Handle decoding more ISO 8601 date/times/periods e.g.
 * R5/2008-03-01T13:00:00Z/P1Y2M10DT2H30M
 */
public class Iso8601Query implements Query
{
    private DateTimeFormatter inputDateFormatter;
    
    public Iso8601Query() {
        this.inputDateFormatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }
    
    public @Override QueryResult getResult(final Context context,
                                           final String query) {
        OffsetDateTime dateTime = null;

        // @todo Run quick check to test if this could be a date.
        
        // Parse yyyy-MM-dd'T'HH:mm:ss.SSSXXX.
        try {
            dateTime = OffsetDateTime.parse(query, this.inputDateFormatter);
        }
        catch (DateTimeParseException exception) {
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

        final ZoneId zoneId =
            context.getZoneId() != null? context.getZoneId() :
            ZoneId.systemDefault();
        final ZonedDateTime zonedDateTime =
            dateTime.atZoneSameInstant(zoneId);
        final DateTimeFormatter outputFormatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL,
                                                  FormatStyle.FULL);

        // @todo Return a title and link to Wikipedia.
        return new SingleQueryResult(query,
                                     outputFormatter.format(zonedDateTime));
    }
}
