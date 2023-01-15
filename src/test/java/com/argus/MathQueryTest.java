package com.argus;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests MathQuery class.
 */
public class MathQueryTest {

    @Test public void testBasicMath() {
        MathQuery query = new MathQuery();
        Context context = new Context(null);
        SingleQueryResult result =
            (SingleQueryResult)query.getResult(context, "2 + 3");
        
        assertEquals("5.0", result.getResult());
    }
    
    @Test public void testIgnoresNonMathQueries() {
        MathQuery query = new MathQuery();
        Context context = new Context(null);

        // The math query should not treat words as variables as it
        // has a tendency to convert this to "ignore * these * words".
        assertNull(query.getResult(context,
                                   "ignore these words"));
    }
}
