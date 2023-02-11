package com.argus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Query that decodes and displays colours.
 *
 * @todo Support rgba().
 * @todo Support #rgb as well as existing #rrggbb.
 * @todo Print CSS colour name if it matches; or come up with
 * a colour description otherwise.
 * @todo Support colour palettes.
 * @todo Convert RGB to HSV and vice versa.
 * @todo Return a title so that it's clear that a colour is returned
 * in the b/g which might not be obvious if the colour is similar
 * to the default b/g colour.
 */
public class ColourQuery implements Query
{
    private Pattern hexColourPattern =
        Pattern.compile("#[a-fA-F0-9]{6}+");
    private Pattern rgbPattern =
        Pattern.compile("rgb\\(([0-9]{1,3}),([0-9]{1,3}),([0-9]{1,3})\\)");
    
    public @Override QueryResult getResult(final Context context,
                                           final String query) {
        try {
            if (hexColourPattern.matcher(query).matches()) {
                final int red = Integer.parseInt(query.substring(1, 3), 16);
                final int green = Integer.parseInt(query.substring(3, 5), 16);
                final int blue = Integer.parseInt(query.substring(5, 7), 16);
                return new ColourQueryResult(query, red, green, blue);
            }
        }
        catch (NumberFormatException exception) {
        }

        // Remove white space characters.
        final String normalisedQuery =
            query.toLowerCase().replaceAll("\\s", "");

        try {
            final Matcher matcher = rgbPattern.matcher(normalisedQuery);
            if (matcher.matches()) {
                final int red = Integer.parseInt(matcher.group(1));
                final int green = Integer.parseInt(matcher.group(2));
                final int blue = Integer.parseInt(matcher.group(3));

                if (red >= 0 && red < 256 &&
                    green >= 0 && green < 256 &&
                    blue >= 0 && blue < 256) {
                    return new ColourQueryResult(query, red, green, blue);
                }
            }
        }
        catch (NumberFormatException exception) {
            // Not possible as the regex would have not matched.
        }
        
        return null;
    }
}
