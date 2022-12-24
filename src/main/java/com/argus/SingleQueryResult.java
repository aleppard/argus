package com.argus;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.http.HttpServletResponse;

import freemarker.template.Template;

/**
 * Representation of a single value query result.
 */
public class SingleQueryResult implements QueryResult {

    private static final Logger LOGGER =
        Logger.getLogger(SingleQueryResult.class.getName());
    
    private String query;
    private String result;

    /**
     * @param query the user's query.
     * @param result the result for that query.
     */
    public SingleQueryResult(final String query, final String result) {
        this.query = query;
        this.result = result;
    }

    @Override public void setResponse(HttpServletResponse response)
        throws IOException {

        TemplateEngine templateEngine = new TemplateEngine();
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("query", query);
        arguments.put("result", result);
        try {
            Template template =
                templateEngine.getTemplate("web/single-query-result.html");
            template.process(arguments, response.getWriter());
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Error returning single query result.",
                       exception);            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
     }
}
