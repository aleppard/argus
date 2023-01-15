import org.junit.Test;

import static org.junit.Assert.assertNotNull;

import com.argus.Context;
import com.argus.JwtDecoderQuery;
import com.argus.QueryResult;

/**
 * Tests JwtDecoderQuery class.
 */
public class JwtDecoderQueryTest {

    @Test public void test() {
        JwtDecoderQuery query = new JwtDecoderQuery();
        Context context = new Context(null);
        
        final QueryResult result = query.getResult(context,
                                                   "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        assertNotNull(result);
    }

}
