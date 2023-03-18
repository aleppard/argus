////////////////////////////////////////////////////////////////////////////////
package com.argus.resolver;

import java.math.BigDecimal;

import java.util.Set;

import io.github.qudtlib.model.Unit;

/**
 * Parser for parsing a string that contains a number and a unit,
 *  e.g. "12°C".
 */
public class QuantityValueParser
{
    /**
     * Parse a string that contains a number and a unit.
     */
    public static ParsedQuantityValue fromString(final String string) {
        try {
            final String amountString = extractNumberString(string);
            if (amountString.length() == 0) return null;
            
            BigDecimal amount = new BigDecimal(amountString);
            
            final String symbolOrName =
                string.substring(amountString.length()).trim();

            Set<Unit> units = UnitParser.fromSymbol(symbolOrName);
            if (units.isEmpty()) {
                units = UnitParser.fromName(symbolOrName);
            }

            if (units.isEmpty()) return null;

            return new ParsedQuantityValue(amount, units);
        }
        catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * Given an embedded number in a larger string return the string
     * version of the number, e.g. "12 horses" would return "12".
     */
    private static String extractNumberString(final String string) {
        // @todo Come up with a grammar to describe the number and
        // use a proper tokeniser and parser.
        // @todo Support the format that BigDecimal uses, and ultimately
        // other numeric formats, e.g. "-1.23E-12".
        int offset = 0;

        while (offset < string.length()) {
            final char character = string.charAt(offset);
            
            if (!Character.isDigit(character) &&
                character != '.' &&
                character != '-') {
                break;
            }
            offset++;
        }

        return string.substring(0, offset);
    }
}
