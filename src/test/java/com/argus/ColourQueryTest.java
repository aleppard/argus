package com.argus;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests ColourQuery class.
 */
public class ColourQueryTest {
    Context context = new Context(null);
    ColourQuery query = new ColourQuery();
    
    @Test public void testHexColour() {
        ColourQueryResult result =
            (ColourQueryResult)query.getResult(context, "#123456");
        assertEquals(18, result.getRed());
        assertEquals(52, result.getGreen());
        assertEquals(86, result.getBlue());        
    }
    
    @Test public void testRgbColour() {
        ColourQueryResult result =
            (ColourQueryResult)query.getResult(context, "rgb(1, 10, 100)");
        assertEquals(1, result.getRed());
        assertEquals(10, result.getGreen());
        assertEquals(100, result.getBlue());                
    }
}
