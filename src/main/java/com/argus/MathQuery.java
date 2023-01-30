package com.argus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
 */
public class MathQuery implements AutoCloseable, Query
{
    private static final Logger LOGGER =
        Logger.getLogger(MathQuery.class.getName());
    
    private ExprEvaluator expressionEvaluator =
        new ExprEvaluator(false, (short)1);

    // Make sure the query contains a mathematical symbol even if it's
    // just a parenthesis.
    private Pattern notLetterOrNumberOrBasicPunctuation =
        Pattern.compile("[^0-9a-zA-Z ,\\.\\?]");

    public MathQuery() {
        // Return numeric results not just symbolic results.
        // @todo Why doesn't this work?
        // EvalEngine.get().setNumericMode(true);
    }

    /**
     * Normalise the input to the math query engine or the output so
     * that we can spot useless transformations like this:
     * "times are tough!" -> "are*Times*tough!".
     */
    public static String normalise(String string, final String separator) {
        string = string.toLowerCase().trim();
        List<String> keywords = Arrays.asList(string.split(separator));
        // @todo Change, e.g. "5000.00" to "5000".
        Collections.sort(keywords);
        return String.join("*", keywords);
    }
    
    public @Override QueryResult getResult(final Context contet,
                                           final String query) {

        // Ignore any query that doesn't contain symbols (i.e. not
        // letters or numbers). We need to do this otherwise text like
        // "post code 5000" would return "5000.0*code*post" whichi
        // is nonsense. Note that the normalise() check performed
        // below will ignore that result if the @todo in that function
        // is addressed.
        
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
            if (resultString.equalsIgnoreCase(query)) {
                return null;
            }
            
            // If all the query has done is rearrange the words and add
            // asterixes, e.g.
            // "times are tough!" -> "are*Times*tough!" then we can ignore it.
            if (normalise(query, " ")
                .equals(normalise(resultString, "\\*"))) {
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
