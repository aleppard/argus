////////////////////////////////////////////////////////////////////////////////
package com.argus.resolver;

import java.math.BigDecimal;

import java.util.Set;

import io.github.qudtlib.model.Unit;

/**
 * The result of parsing a strig that contains a number and a unit,
 * e.g. "12in".
 */
public class ParsedQuantityValue
{
    public BigDecimal amount;

    // Some units may be ambiguous, e.g. is F Fahrenheit or Farad? So
    // this is the set of possible units.
    public Set<Unit> possibleUnits;

    public ParsedQuantityValue(BigDecimal amount, Set<Unit> possibleUnits) {
        this.amount = amount;
        this.possibleUnits = possibleUnits;
    }
}
