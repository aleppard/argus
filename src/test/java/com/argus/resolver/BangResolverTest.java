////////////////////////////////////////////////////////////////////////////////
package com.argus.resolver;

import com.argus.Context;
import com.argus.RedirectionQueryResult;
import com.argus.Query;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests BangResolver class.
 */
public class BangResolverTest {

    private BangResolver resolver = new BangResolver();
    private Context context = new Context(null);
    
    @Test public void test() {
        final Query query = new Query(context, "!g search me on google");
        RedirectionQueryResult result =
            (RedirectionQueryResult)resolver.tryResolve(query);
        assertEquals("https://google.com/search?q=+search+me+on+google",
                     result.getUrl());
    }

}
