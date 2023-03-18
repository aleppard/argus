////////////////////////////////////////////////////////////////////////////////
package com.argus.resolver;

import com.argus.Query;
import com.argus.QueryResult;
import com.argus.TableQueryResult;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.util.logging.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.qudtlib.model.Unit;
import io.github.qudtlib.model.QuantityValue;

/**
 * Resolver the resolves unit conversion queries, e.g. "inches to feet".
 *
 * @todo Convert "one" to "1", "1/2" to "0.5" etc.
 * @todo Support "TO" and "IN" as well as "to" and "in".
 */
public class ConversionResolver implements Resolver
{
    private static final Logger LOGGER =
        Logger.getLogger(ConversionResolver.class.getName());

    /**
     * Given two sets of units find compatible unit pairs between both
     * sets.
     */
    private List<Unit[]> findConvertibleUnitPairs(final Set<Unit> firstSet,
                                                  final Set<Unit> secondSet) {
        List<Unit[]> unitPairs = new ArrayList<>();

        for (final Unit firstUnit : firstSet) {
            for (final Unit secondUnit: secondSet) {
                if (firstUnit.isConvertible(secondUnit)) {
                    unitPairs.add(new Unit[]{firstUnit, secondUnit});
                }
            }
        }
        
        return unitPairs;
    }

    /**
     * Parse a unit by its symbol or name.
     */
    private Set<Unit> parseUnit(String string) {
        string = string.trim();

        {
            final Set<Unit> units = UnitParser.fromSymbol(string);
            if (!units.isEmpty()) { return units; }
        }
        
        {
            Set<Unit> units = UnitParser.fromName(string);
            if (!units.isEmpty()) { return units; }
        }

        return null;
    }
    
    /**
     * Parse either a quanity value (e.g. an amount and unit), or just
     * a unit (by either name or symbol). If there is no amount then
     * the returned amount will be set to null. If parsing fails this
     * will return null.
     */
    private ParsedQuantityValue parseUnitOrQuantityValue(String string) {
        string = string.trim();
        ParsedQuantityValue value = 
            QuantityValueParser.fromString(string);
        if (value != null) { return value; }

        final Set<Unit> units = parseUnit(string);
        if (units != null) {
            return new ParsedQuantityValue(null, units);
        }

        return null;
    }
    
    private static String quantityAndUnitToString(final BigDecimal amount,
                                                  final Unit unit) {
        return amount.toString() + unit.toString();
    }

    /** Convert from a unit and amount to another unit. */
    private static String convertAmountoUnit(final QuantityValue source,
                                             final Unit target) {
        final BigDecimal convertedAmount =
            source.convert(target).getValue();
        final String sourceString =
            quantityAndUnitToString(source.getValue(), source.getUnit());
        final String targetString =
            quantityAndUnitToString(convertedAmount, target);
        
        return sourceString + " = " +  targetString;
    }

    /** Convert from one unit to another unit with an amount. */
    private static String convertUnitToAmount(final Unit source,
                                              final QuantityValue target) {
        final BigDecimal convertedAmount =
            target.convert(source).getValue();
        final String sourceString =
            quantityAndUnitToString(convertedAmount, source);
        final String targetString =
            quantityAndUnitToString(target.getValue(), target.getUnit());

        return sourceString + " = " + targetString;
    }

    /** Convert from one unit to another. */
    private static String convertUnits
        (final Unit source,
         final Unit target) {
        BigDecimal convertedAmount = source.convert(BigDecimal.ONE, target);
            
        final String sourceString =
            quantityAndUnitToString(BigDecimal.ONE, source);
        final String targetString =
            quantityAndUnitToString(convertedAmount, target);

        return sourceString + " = " + targetString;
    }        

    /**
     * Calculate "to" queries, e.g. "mi TO km".
     */
    private static String convertTo(final BigDecimal sourceAmount,
                                    final BigDecimal targetAmount,
                                    final Unit sourceUnit,
                                    final Unit targetUnit) {
        String conversionString = null;
        
        if (sourceAmount != null &&
            targetAmount == null) {
            final QuantityValue source =
                new QuantityValue(sourceAmount, sourceUnit);
            conversionString = convertAmountoUnit(source, targetUnit);
        }
        else if (sourceAmount == null &&
                 targetAmount == null) {
            conversionString = convertUnits(sourceUnit, targetUnit);
        }
        else {
            // Ignore.
        }

        return conversionString;
    }

    /**
     * Calculate "in" queries, e.g. "feet IN inches".
     */
    private static String calculateIn(final BigDecimal sourceAmount,
                                      final BigDecimal targetAmount,
                                      final Unit sourceUnit,
                                      final Unit targetUnit) {
        String conversionString = null;
        
        if (sourceAmount != null &&
            targetAmount == null) {
            final QuantityValue source =
                new QuantityValue(sourceAmount, sourceUnit);
            conversionString = convertAmountoUnit(source, targetUnit);
        }
        else if (sourceAmount == null &&
                 targetAmount != null) {
            final QuantityValue target =
                new QuantityValue(targetAmount, targetUnit);
            conversionString = convertUnitToAmount(sourceUnit, target);
        }
        else if (sourceAmount == null &&
                 targetAmount == null) {
            conversionString = convertUnits(sourceUnit, targetUnit);
        }
        else {
            // Ignore.
        }

        return conversionString;
    }

    /**
     * Given a string find the "bridge" to describe conversion between
     * units (with or without values), e.g. for "feet in inches" the
     * bridge is "in".
     */
    private static String findBridge(final String queryString) {
        String[] bridges = new String[]{"in an", "in a", "in", "to"};
        for (final String bridge : bridges) {
            if (queryString.contains(" " + bridge + " ")) {
                return bridge;
            }
        }

        return null;
    }

    public @Override QueryResult tryResolve(final Query query) {
        final String queryString = query.getRawString();

        final String bridge = findBridge(queryString);
        if (bridge == null) return null;

        final String bridgeWithSpaces = " " + bridge + " ";
        final int indexOfBridge = queryString.indexOf(bridgeWithSpaces);

        final ParsedQuantityValue parsedLhs =
            parseUnitOrQuantityValue(queryString.substring(0, indexOfBridge));
        
        if (parsedLhs == null) return null;
        
        final ParsedQuantityValue parsedRhs =
            parseUnitOrQuantityValue
            (queryString.substring(indexOfBridge + bridgeWithSpaces.length()));
        if (parsedRhs == null) return null;
        
        final List<Unit[]> unitPairs = 
            findConvertibleUnitPairs(parsedLhs.possibleUnits,
                                     parsedRhs.possibleUnits);
        if (unitPairs.isEmpty()) return null;
        
        TableQueryResult result = new TableQueryResult(query);
        for (final Unit[] unitPair : unitPairs) {
            String conversionString = null;

            if (bridge.equals("to")) {
                conversionString = convertTo(parsedLhs.amount, parsedRhs.amount,
                                             unitPair[0], unitPair[1]);
            }
            else {
                conversionString = calculateIn(parsedRhs.amount,
                                               parsedLhs.amount,
                                               unitPair[1], unitPair[0]);
            }
                                
            if (conversionString != null) {
                result.addRow(conversionString);
            }
        }
        
        if (result.getRowCount() == 0) {
            return null;
        }

        return result;
    }
}
