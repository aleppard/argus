////////////////////////////////////////////////////////////////////////////////
package com.argus.resolver;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.qudtlib.model.LangString;
import io.github.qudtlib.model.Unit;
import io.github.qudtlib.model.Units;

import io.github.qudtlib.Qudt;

/**
 * Class for parsing units (e.g. "lb") from either their unit or
 * name (e.g. "pound").
 *
 * Note this does not use any of the 3 JSR proposed standards for unit 
 * conversion. There were issues with each reference implementation.
 * In one case the reference implementation did not contain any imperial
 * measurements.
 */
public class UnitParser
{
    // Custom unit names that aren't supported by Qudt but we'd like
    // to match.
    // @todo Add fahrenheit.
    private static Map<String, Unit> customUnitNames =
        Map.of("celsius", Units.DEG_C,
               "kilos", Units.KiloGM,
               "feet", Units.FT,
               "inches", Units.IN,
               "pound", Units.LB,
               "pounds", Units.LB,
               "sec", Units.SEC,
               "secs", Units.SEC,
               "min", Units.MIN,
               "mins", Units.MIN);

    // Custom unit symbols that aren't supported by Qudt but we'd like
    // to match.
    // @todo Qudt wants to use 'lbm' for pounds. How can we use 'lb' in
    // the Qudt ontology?
    private static Map<String, Unit> customUnitSymbols = 
        Map.of("lb", Units.LB,
               "kph", Units.KiloM__PER__HR);
               // No miles per hour?

    /**
     * Relax symbols to make them more easily matchable. For example users may
     * not include the degree symbol or may use 'u' instead of the micro
     * symbol.
     */
    private static String relaxUnitSymbol(final String input) {
        String output = input;

        output = output.replace('µ', 'u');
        output = output.replace("°", "");

        // @todo Should we support this? Could this lead to erroneous matches?
        output = output.replace("⋅", "");
        
        return output;
    }

    /**
     * There are a variety of ways a user can enter a unit symbol. Try
     * to normalise the user's input towards the standard used by Qudt.
     */
    private static String normaliseUserInput(final String input) {
        String output = input;

        output = output.replace("^2", "²"); // e.g. m^2 -> m²
        output = output.replace("^3", "³"); // e.g. m^3 -> m³
        output = output.replace("^4", "⁴"); // e.g. m^4 -> m⁴
        output = output.replace("^5", "⁵"); // e.g. m^5 -> m⁵
        output = output.replace("^6", "⁶"); // e.g. m^6 -> m⁶

        output = output.replace('2', '²'); // e.g. m2 -> m²
        output = output.replace('3', '³'); // e.g. m3 -> m³
        output = output.replace('4', '⁴'); // e.g. m4 -> m⁴
        output = output.replace('5', '⁵'); // e.g. m5 -> m⁵
        output = output.replace('6', '⁶'); // e.g. m6 -> m⁶
        
        output = output.replace('.', '⋅');
        output = output.replace('_', '⋅');

        // Replace Greek letter mau with micro character.
        output = output.replace('μ', 'µ');
        
        // @todo Support negative exponent variants, e.g. m³⋅s⁻¹ -> m³/s

        return output;
    }

    /**
     * Return all the units that match the given symbol
     */
    public static Set<Unit> fromSymbol(final String symbol) {
        Set<Unit> matchedUnits = new HashSet<>();
        String normalisedUserSymbol = normaliseUserInput(symbol);

        // First try our custom symbols.
        {
            final Unit unit =
                customUnitSymbols.get(normalisedUserSymbol.toLowerCase());
            if (unit != null) matchedUnits.add(unit);
        }

        // Next try matched normalised user input.
        for (final Unit unit : Qudt.allUnits()) {
            final String unitSymbol = unit.getSymbol().orElse(null);
            if (unitSymbol != null) {
                if (unitSymbol.equals(normalisedUserSymbol)) {
                    matchedUnits.add(unit);
                }
            }
        }

        // Try matching a relaxed version of the symbol. We still
        // want to do this even if we have a match as the user may
        // mean, say, Fahrenheit rather than Farad.
        for (final Unit unit : Qudt.allUnits()) {
            final String unitSymbol = unit.getSymbol().orElse(null);
            if (unitSymbol != null) {
                if (relaxUnitSymbol(unitSymbol)
                    .equals(normalisedUserSymbol)) {
                    matchedUnits.add(unit);
                }
            }
        }

        // If we don't find anything try matching against an upper
        // case version of the symbol. Note we match "LB" to "lb" but NOT
        // "C" to "c".
        if (matchedUnits.isEmpty()) {
            for (final Unit unit : Qudt.allUnits()) {
                final String unitSymbol = unit.getSymbol().orElse(null);
                if (unitSymbol != null) {
                    if (unitSymbol.toUpperCase()
                        .equals(normalisedUserSymbol)) {
                        matchedUnits.add(unit);
                    }
                }
            }
        }

        return matchedUnits;
    }

    /**
     * Return all the units that match the given unit name.
     *
     * @todo Currently no more than one unit is returned but in
     * future multiple could be returned, e.g. "lb" could match
     * pound weight and pound force.
     */    
    public static Set<Unit> fromName(final String name) {
        String normalisedName = name.toLowerCase();

        // If the name ends in an 's' it could be plural, e.g. metres
        // instead of metre.
        final boolean nameMayBePlural = normalisedName.endsWith("s");

        // First try our custom names.
        {
            final Unit unit = customUnitNames.get(normalisedName);
            if (unit != null) return Set.of(unit);
        }

        for (final Unit unit : Qudt.allUnits()) {
            for (final LangString label : unit.getLabels()) {
                final String normalisedLabel = label.getString().toLowerCase();
                
                if (normalisedLabel.equals(normalisedName)) {
                    return Set.of(unit);
                }

                // @todo This doesn't support 'es' plurals, e.g.
                // "inches".
                if (nameMayBePlural) {
                    final String normalisedLabelPlusS = normalisedLabel + "s";
                    
                    if (normalisedLabelPlusS.equals(normalisedName)) {
                        return Set.of(unit);
                    }
                }
            }
        }

        return new HashSet<>();
    }
}
