package com.argus;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests MathQuery class.
 */
public class MathQueryTest {

    private MathQuery query = new MathQuery();
    private Context context = new Context(null);

    private String getResult(final String queryString) {
        QueryResult result = query.getResult(context, queryString);
        if (result == null) return null;
        
        assertTrue(result instanceof SingleQueryResult);
        return ((SingleQueryResult)result).getResult();
    }
    
    @Test public void testBasicMath() {
        assertEquals("5.0", getResult("2 + 3"));
        assertEquals("1.*10^-9", getResult("1/1000000000"));
        assertEquals("-3.6288*10^6", getResult("-10!"));                
        assertEquals("3.6288*10^6", getResult("10!"));
        assertEquals("0.5", getResult("sin(pi/6)"));
        assertEquals("6.28319", getResult("pi + pi"));
    }
    
    @Test public void testIgnoresNonMathQueries() {
        // The math query should not treat words as variables as it
        // has a tendency to convert this to "ignore * these * words".
        assertNull(query.getResult(context,
                                   "ignore these words"));
        assertNull(query.getResult(context,
                                   "ignore.these.words"));
        assertNull(query.getResult(context,
                                   "ignore/these/words"));
        assertNull(query.getResult(context,
                                   "ignore/these/words 5"));
        assertNull(query.getResult(context,
                                   "model-5123u manual"));
    }
}
