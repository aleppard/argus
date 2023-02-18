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
 * Tests BaseConversionResolver class.
 */
public class BaseConversionResolverTest {

    private BaseConversionResolver resolver = new BaseConversionResolver();
    private Context context = new Context(null);

    private String tryResolve(final String queryString) {
        final Query query = new Query(context, queryString);
        QueryResult result = resolver.tryResolve(query);
        if (result == null) return null;
        
        assertTrue(result instanceof SingleQueryResult);
        return ((SingleQueryResult)result).getResult();
    }
    
    @Test public void testConversions() {
        assertEquals("0xA", tryResolve("10 in hex"));
        assertEquals("0b1010", tryResolve("10 in binary"));
        assertEquals("0xA", tryResolve("1010 binary to hex"));
        assertEquals("255", tryResolve("convert 0xFF to dec"));
        assertEquals("254", tryResolve("convert FE to dec"));
    }
}
