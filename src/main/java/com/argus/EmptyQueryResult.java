package com.argus;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.http.HttpServletResponse;

import freemarker.template.Template;

/**
 * Representation of an empty query result, i.e. a query that had
 * no results.
 */
public class EmptyQueryResult implements QueryResult {

    private static final Logger LOGGER =
        Logger.getLogger(EmptyQueryResult.class.getName());

    private Query query;

    public EmptyQueryResult(final Query query) {
        this.query = query;
    }
    
    @Override public void setResponse(HttpServletResponse response)
        throws IOException {

         TemplateEngine templateEngine = new TemplateEngine();

         Map<String, Object> arguments = new HashMap<>();
         arguments.put("query", query.getRawString());

         try {
             Template template =
                 templateEngine.getTemplate("templates/empty-query-result.html.ftl");
             response.addHeader("Content-Type", ContentType.HTML);
             template.process(arguments, response.getWriter());
             response.setStatus(HttpServletResponse.SC_OK);
         }
         catch (Exception exception) {
             LOGGER.log(Level.SEVERE, "Error returning empty query result.",
                        exception);            
             response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         }
     }
}
