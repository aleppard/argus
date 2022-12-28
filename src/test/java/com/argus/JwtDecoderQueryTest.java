import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.argus.Context;
import com.argus.JwtDecoderQuery;
import com.argus.QueryResult;

/**
 * Tests JwtDecoderQuery class.
 */
public class JwtDecoderQueryTest {

    // @todo For some reason this test case is not run by 'mvn test'?
    @Test void test() {
        JwtDecoderQuery query = new JwtDecoderQuery();
        Context context = new Context(null);
        
        final QueryResult result = query.getResult(context,
                                                   "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        assertNotNull(result);
    }

}
