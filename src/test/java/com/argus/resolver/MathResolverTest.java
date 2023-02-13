package com.argus.resolver;

import com.argus.Context;
import com.argus.SingleQueryResult;
import com.argus.Query;
import com.argus.QueryResult;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests MathQuery class.
 */
public class MathResolverTest {

    private MathResolver resolver = new MathResolver();
    private Context context = new Context(null);

    private String tryResolve(final String queryString) {
        final Query query = new Query(context, queryString);
        QueryResult result = resolver.tryResolve(query);
        if (result == null) return null;
        
        assertTrue(result instanceof SingleQueryResult);
        return ((SingleQueryResult)result).getResult();
    }

    @Test public void testBasicMath() {
        assertEquals("5.0", tryResolve("2 + 3"));
        assertEquals("1.*10^-9", tryResolve("1/1000000000"));
        assertEquals("-3.6288*10^6", tryResolve("-10!"));                
        assertEquals("3.6288*10^6", tryResolve("10!"));
        assertEquals("0.5", tryResolve("sin(pi/6)"));
        assertEquals("6.28319", tryResolve("pi + pi"));
    }
    
    @Test public void testIgnoresNonMathQueries() {
        // The math query should not treat words as variables as it
        // has a tendency to convert this to "ignore * these * words".
        assertNull(resolver.tryResolve(new Query(context,
                                                 "ignore these words")));
        assertNull(resolver.tryResolve(new Query(context,
                                                 "ignore.these.words")));
        assertNull(resolver.tryResolve(new Query(context,
                                                 "ignore/these/words")));
        assertNull(resolver.tryResolve(new Query(context,
                                                 "ignore/these/words 5")));
        assertNull(resolver.tryResolve(new Query(context,
                                                 "model-5123u manual")));
    }
}
