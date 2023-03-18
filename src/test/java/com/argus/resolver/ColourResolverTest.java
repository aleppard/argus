////////////////////////////////////////////////////////////////////////////////
package com.argus.resolver;

import com.argus.ColourQueryResult;
import com.argus.Context;
import com.argus.Query;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests ColourResolver class.
 */
public class ColourResolverTest {
    private Context context = new Context(null);
    private ColourResolver resolver = new ColourResolver();
    
    @Test public void testHexColour() {
        ColourQueryResult result =
            (ColourQueryResult)resolver.tryResolve(new Query(context, "#123456"));
        assertEquals(18, result.getRed());
        assertEquals(52, result.getGreen());
        assertEquals(86, result.getBlue());        
    }
    
    @Test public void testRgbColour() {
        ColourQueryResult result =
            (ColourQueryResult)resolver.tryResolve(new Query(context, "rgb(1, 10, 100)"));
        assertEquals(1, result.getRed());
        assertEquals(10, result.getGreen());
        assertEquals(100, result.getBlue());                
    }
}
