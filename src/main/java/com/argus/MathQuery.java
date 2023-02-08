package com.argus;

import java.util.logging.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;

import org.matheclipse.core.interfaces.IExpr;

/**
 * Handle math queries.
 *
 * @todo Support sin 30, sine of 30 etc.
 * @todo Do more preprocessing, e.g. "plus" -> "+", "sine" -> "sin(x)" etc.
 * @todo Handle x << y, y >> x.
 * @todo Support symbolic processing, e.g. queries about differentiation,
 * integration etc which the underlying library supports. Then return the 
 * results using MathML to display properly in the browser.
 * @todo Increase precision of returned numbers.
 * @todo Can we output numbers with an exponent instead of "^"?
 */
public class MathQuery implements AutoCloseable, Query
{
    private static final Logger LOGGER =
        Logger.getLogger(MathQuery.class.getName());
    
    private ExprEvaluator expressionEvaluator =
        new ExprEvaluator(false, (short)1);

    // Make sure the query contains a mathematical symbol even if it's
    // just a parenthesis.
    private Pattern notLetterOrNumberOrBasicPunctuationPattern =
        Pattern.compile("[^0-9a-zA-Z ,\\.\\?]");

    // Matches numbers returned from the math query library, e.g.
    // 5.0
    // 1.*10^-9 (???)
    // 3.6288*10^6
    // -3.6288*10^6
    private Pattern numberPattern =
        Pattern.compile("-?[0-9]+\\.[0-9]*(\\*[0-9]+\\^-?[0-9]+)?");
    
    public MathQuery() {
        // Return numeric results not just symbolic results.
        // @todo Why doesn't this work?
        // EvalEngine.get().setNumericMode(true);
    }

    public @Override QueryResult getResult(final Context contet,
                                           final String query) {

        // Ignore any query that doesn't contain symbols (i.e. not
        // letters or numbers). We do this because the math library
        // has a tendency to try its best with non-math queries.
        // See comment below.

        // @todo Is this check necessary now we have the check below?
        if (!notLetterOrNumberOrBasicPunctuationPattern.matcher(query).find()) {
            return null;
        }

        // Return the numeric result of the query.
        final String numericQuery = "N(" + query + ")";
        
        try {
            final IExpr result = expressionEvaluator.eval(numericQuery);
            final String resultString = result.toString();

            // Ignore result if it's the same as the input.
            if (resultString.equalsIgnoreCase(query)) {
                return null;
            }

            // The math library has a tendency to try its best with
            // non-math queries, e.g. 
            // "times are tough!" might be converted to "are*Times*tough!".
            // So ignore the result if the output is not a number and is
            // not smaller than the input.
            if (!numberPattern.matcher(resultString).matches() &&
                resultString.length() >= query.length()) {
                return null;
            }
            
            return new SingleQueryResult(query, result.toString());
        }
        catch (Exception exception) {
        }

        return null;
    }

    @Override public void close() {
        // 3-Jan-2023 23:49:17.248 SEVERE [Thread-1] org.apache.catalina.loader.WebappClassLoaderBase.checkThreadLocalMapForLeaks The web application [ROOT] created a ThreadLocal with key of type [java.lang.ThreadLocal.SuppliedThreadLocal] (value [java.lang.ThreadLocal$SuppliedThreadLocal@27b74311]) and a value of type [org.matheclipse.core.eval.EvalEngine] (value [org.matheclipse.core.eval.EvalEngine@23bfc29c]) but failed to remove it when the web application was stopped. Threads are going to be renewed over time to try and avoid a probable memory leak.
    }
}
