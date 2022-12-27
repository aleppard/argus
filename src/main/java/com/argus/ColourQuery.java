package com.argus;

import java.util.regex.Pattern;

/**
 * Query that decodes and displays colours.
 *
 * @todo Support other colour encodings such as "#xxx", rgb(r, g, b),
 * rgba(r, g, b, a) etc.
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
    private Pattern hexColour =
        Pattern.compile("#[a-fA-F0-9]{6}+");
    
    public @Override QueryResult getResult(final Context context,
                                           final String query) {
        if (!hexColour.matcher(query).matches()) {
            return null;
        }

        return new ColourQueryResult(query, query);
    }
}
