package com.argus.resolver;

import com.argus.Context;
import com.argus.SingleQueryResult;
import com.argus.TableQueryResult;
import com.argus.Query;
import com.argus.QueryResult;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests ConversionResolver class.
 */
public class ConversionResolverTest {

    private ConversionResolver resolver = new ConversionResolver();
    private Context context = new Context(null);
    
    private String tryResolve(final String queryString) {
        final Query query = new Query(context, queryString);
        QueryResult result = resolver.tryResolve(query);
        if (result == null) return null;
        
        if (result instanceof SingleQueryResult) {
            return ((SingleQueryResult)result).getResult();
        }
        else if (result instanceof TableQueryResult) {
            final TableQueryResult tableQueryResult =
                (TableQueryResult)result;
            String resultString = "";
            for (int row = 0; row < tableQueryResult.getRowCount(); row++) {
                if (resultString.length() > 0) {
                    resultString += ";";
                }
                resultString += tableQueryResult.getCellText(row, 0);
            }
            return resultString;
        }
        else {
            return null;
        }
    }

    @Test public void testToConversions() {
        assertEquals("1mi = 1.609344km", tryResolve("mi to km"));
        assertEquals("12in = 1ft", tryResolve("12in to feet"));
    }

    @Test public void testInConversions() {
        assertEquals("1yd = 3ft", tryResolve("feet in yard"));
        assertEquals("1m = 100cm", tryResolve("cm in metre"));
        assertEquals("1m = 100cm", tryResolve("cm in 1 metre"));

        // @todo Drop the decimal point here.
        assertEquals("10.0m = 1000cm", tryResolve("1000cm in metres"));
        
        // @todo Also support "m in cm" giing 100 not 0.01.        
        assertEquals("1cm = 0.01m", tryResolve("m in 1cm"));
        
        assertEquals("1s = 1000ms", tryResolve("ms in s"));
        assertEquals("1m = 1000mm", tryResolve("mm in m"));
        
        // @todo Would prefer that it say "2lb".
        assertEquals("2lbm = 32oz", tryResolve("oz in 2 lb"));

        // @todo Don't display so many decimal points here.
        assertEquals("148.8886705555555893186492°C = 300°F",
                     tryResolve("300°F in °C"));
        assertEquals("148.8886705555555893186492°C = 300°F",
                     tryResolve("300F in celsius"));

        // Test case where there are two possible conversions.
        assertEquals("28945601.970C = 300F;" +
                     "148.8886705555555893186492°C = 300°F",
                     tryResolve("300F in C"));        
    }
}
