////////////////////////////////////////////////////////////////////////////////
package com.argus.resolver;

import java.util.Set;

import io.github.qudtlib.model.Unit;
import io.github.qudtlib.model.Units;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Tests UnitParser class. */
public class UnitParserTest {
    
    /** 
     * Test that we can match standard unit names verbatim, ignoring case
     * and plurals.
     */
    @Test public void testFromName() {
        assertEquals(Set.of(Units.CentiM), UnitParser.fromName("centimetres"));
        assertEquals(Set.of(Units.CentiM), UnitParser.fromName("centimetre"));        
        assertEquals(Set.of(Units.CentiM), UnitParser.fromName("centimeter"));
        assertEquals(Set.of(Units.CentiM), UnitParser.fromName("Centimeter"));
        
        assertEquals(Set.of(Units.SEC), UnitParser.fromName("second"));
        assertEquals(Set.of(Units.SEC), UnitParser.fromName("SECONDS"));

        assertEquals(Set.of(Units.MIN), UnitParser.fromName("minute"));
        assertEquals(Set.of(Units.MIN), UnitParser.fromName("MINUTES"));
    }

    /** That that we can match custom unit names. */
    @Test public void testFromNameWithCustom() {
        assertEquals(Set.of(Units.IN), UnitParser.fromName("inch"));
        assertEquals(Set.of(Units.IN), UnitParser.fromName("inches"));

        assertEquals(Set.of(Units.LB), UnitParser.fromName("pound"));
        assertEquals(Set.of(Units.LB), UnitParser.fromName("Pounds"));
        
        assertEquals(Set.of(Units.SEC), UnitParser.fromName("sec"));
        assertEquals(Set.of(Units.SEC), UnitParser.fromName("secs"));        
        assertEquals(Set.of(Units.MIN), UnitParser.fromName("min"));
    }
    
    /** Test that we can match standard symbols verbatim. */
    @Test public void testFromSymbol() {
        assertEquals(Set.of(Units.MilliSEC), UnitParser.fromSymbol("ms"));
        assertEquals(Set.of(Units.SEC), UnitParser.fromSymbol("s"));
        assertEquals(Set.of(Units.OZ), UnitParser.fromSymbol("oz"));
    }    

    /** Test that we match custom symbols. */
    @Test public void testFromSymbolWithCustomSymbol() {
        assertEquals(Set.of(Units.LB), UnitParser.fromSymbol("lb"));
        assertEquals(Set.of(Units.LB), UnitParser.fromSymbol("LB"));
    }
    
    /**
     * Test that we match upper case versions of symbols.
     */
    @Test public void testFromSymbolWithUpperCase() {
        // Test that it matches upper case matches.
        assertEquals(Set.of(Units.M__PER__SEC), UnitParser.fromSymbol("M/S"));

        // But not lower case matches, e.g. don't match just 'f' as Fahrenheit.
        assertEquals(Set.of(), UnitParser.fromSymbol("f"));
    }    
    
    /**
     * Test that we match various representations of the dot between
     * units in a symbol.
     */
    @Test public void testFromSymbolWithDot() {
        // Test verbatim match.
        assertEquals(Set.of(Units.KiloGM_F__M__PER__SEC),
                     UnitParser.fromSymbol("kgf⋅m/s"));

        // Test with dot subsitute.
        assertEquals(Set.of(Units.KiloGM_F__M__PER__SEC),
                     UnitParser.fromSymbol("kgf.m/s"));

        // Test with underscore subsitute.
        assertEquals(Set.of(Units.KiloGM_F__M__PER__SEC),
                     UnitParser.fromSymbol("kgf_m/s"));                     

        // Test failsafe matching without any dot.
        assertEquals(Set.of(Units.KiloGM_F__M__PER__SEC),
                     UnitParser.fromSymbol("kgfm/s"));                     
    }
    
    /** 
     * Test that we don't require the degree symbol when matching 
     * temperature.
     */
    @Test public void testFromSymbolWithDegree() {
        // Accept missing degree symbol. This does mean that "F" is an ambiguous
        // symbol. The context should hopefully make it obvious which is
        // required.
        assertEquals(Set.of(Units.FARAD, Units.F, Units.DEG_F),
                     UnitParser.fromSymbol("F"));
        assertEquals(Set.of(Units.DEG_F), UnitParser.fromSymbol("°F"));
    }
    
    /** Test that we match various representation of the micro symbol. */
    @Test public void testFromSymbolWithMicro() {
        // Accept replacement of "u" for "µ". Also accept Greek mu letter.
        assertEquals(Set.of(Units.MicroFARAD), UnitParser.fromSymbol("uF"));
        assertEquals(Set.of(Units.MicroFARAD), UnitParser.fromSymbol("µF"));
        assertEquals(Set.of(Units.MicroFARAD), UnitParser.fromSymbol("μF"));
    }

    /** Test that we match various representations of symbol to a power. */
    @Test public void testFromSymbolWithPowers() {
        assertEquals(Set.of(Units.M2), UnitParser.fromSymbol("m²"));
        assertEquals(Set.of(Units.M3), UnitParser.fromSymbol("m³"));
        assertEquals(Set.of(Units.M4), UnitParser.fromSymbol("m⁴"));
        assertEquals(Set.of(Units.M5), UnitParser.fromSymbol("m⁵"));
        assertEquals(Set.of(Units.M6), UnitParser.fromSymbol("m⁶"));
        
        assertEquals(Set.of(Units.M2), UnitParser.fromSymbol("m2"));
        assertEquals(Set.of(Units.M3), UnitParser.fromSymbol("m3"));
        assertEquals(Set.of(Units.M4), UnitParser.fromSymbol("m4"));
        assertEquals(Set.of(Units.M5), UnitParser.fromSymbol("m5"));
        assertEquals(Set.of(Units.M6), UnitParser.fromSymbol("m6"));

        assertEquals(Set.of(Units.M2), UnitParser.fromSymbol("m^2"));
        assertEquals(Set.of(Units.M3), UnitParser.fromSymbol("m^3"));
        assertEquals(Set.of(Units.M4), UnitParser.fromSymbol("m^4"));
        assertEquals(Set.of(Units.M5), UnitParser.fromSymbol("m^5"));
        assertEquals(Set.of(Units.M6), UnitParser.fromSymbol("m^6"));
    }
}
