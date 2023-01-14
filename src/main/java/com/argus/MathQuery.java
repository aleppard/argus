package com.argus;

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
 * @todo Support symbolic processing, e.g. queries about differentials
 * which the underlying library supports. Then return the results using
 * MathML to display properly in the browser.
 */
public class MathQuery implements Query
{
    // @todo Remove this memory leak on shutdown.
    // 3-Jan-2023 23:49:17.248 SEVERE [Thread-1] org.apache.catalina.loader.WebappClassLoaderBase.checkThreadLocalMapForLeaks The web application [ROOT] created a ThreadLocal with key of type [java.lang.ThreadLocal.SuppliedThreadLocal] (value [java.lang.ThreadLocal$SuppliedThreadLocal@27b74311]) and a value of type [org.matheclipse.core.eval.EvalEngine] (value [org.matheclipse.core.eval.EvalEngine@23bfc29c]) but failed to remove it when the web application was stopped. Threads are going to be renewed over time to try and avoid a probable memory leak.
    private ExprEvaluator expressionEvaluator =
        new ExprEvaluator(false, (short)1);

    // Make sure the query contains a mathematical symbol even if it's
    // just a parenthesis.
    private Pattern notLetterOrNumberOrBasicPunctuation =
        Pattern.compile("[^0-9a-zA-Z,\\.\\?]");

    public MathQuery() {
        // Return numeric results not just symbolic results.
        // @todo Why doesn't this work?
        // EvalEngine.get().setNumericMode(true);
    }
    
    public @Override QueryResult getResult(final Context contet,
                                           final String query) {

        // Ignore any query that doesn't contain symbols (i.e. not
        // letters or numbers). We need to do this otherwise text like
        // "post code 5000" would return "5000.0*code*post" whichi
        // is nonsense.
        
        // This rules out queries like "sin 30" but that's okay because
        // that requires preprocessing too before the library returns
        // the correct answer.
        // @todo This is probably not strict enough.
        if (!notLetterOrNumberOrBasicPunctuation.matcher(query).find()) {
            return null;
        }

        // Return the numeric result of the query.
        final String numericQuery = "N(" + query + ")";
        
        try {
            final IExpr result = expressionEvaluator.eval(numericQuery);
            final String resultString = result.toString();

            // Ignore result if it's the same as the input.
            if (!resultString.equalsIgnoreCase(query)) {
                return new SingleQueryResult(query, result.toString());
            }
        }
        catch (Exception exception) {
        }

        return null;
    }
}
