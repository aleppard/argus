package com.argus.resolver;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

import com.argus.Context;
import com.argus.Query;
import com.argus.QueryResult;

/**
 * Tests JwtDecoderResolver class.
 */
public class JwtDecoderResolverTest {

    private JwtDecoderResolver resolver = new JwtDecoderResolver();
    private Context context = new Context(null);
    
    @Test public void test() {
        final Query query = new Query(context, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        final QueryResult result = resolver.tryResolve(query);
        assertNotNull(result);
    }

}
