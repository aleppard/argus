package com.argus;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.http.HttpServletResponse;

import freemarker.template.Template;

/**
 * Representation of a colour returned to the user as part of a
 * query.
 */
public class ColourQueryResult implements QueryResult {

    private static final Logger LOGGER =
        Logger.getLogger(ColourQueryResult.class.getName());
    
    private String query;
    private String colour;

    /**
     * @param query the user's query.
     * @param colour the colour in the format #RRGGBB in hex.
     */
    public ColourQueryResult(final String query, final String colour) {
        this.query = query;
        this.colour = colour;
    }

     @Override public void setResponse(HttpServletResponse response)
        throws IOException {

        TemplateEngine templateEngine = new TemplateEngine();
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("query", query);
        arguments.put("colour", colour);
        try {
            Template template =
                templateEngine.getTemplate("templates/colour-query-result.html");
            template.process(arguments, response.getWriter());
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Error returning colour query result.",
                       exception);            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
     }

}
