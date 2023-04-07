package com.argus;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.http.HttpServletResponse;

import freemarker.template.Template;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Representation of a single value query result.
 */
public class SingleQueryResult implements QueryResult {

    private static final Logger LOGGER =
        Logger.getLogger(SingleQueryResult.class.getName());
    
    private Query query;
    private String result;
    private boolean fixedWidth = false;

    /**
     * @param query the user's query.
     * @param result the result for that query.
     */
    public SingleQueryResult(final Query query, final String result) {
        this.query = query;
        this.result = result;
    }

    /**
     * @param query the user's query.
     * @param result the result for that query.
     */
    public SingleQueryResult(final Query query, final String result,
                             boolean fixedWidth) {
        this.query = query;
        this.result = result;
        this.fixedWidth = fixedWidth;
    }

    public String getResult() {
        return this.result;
    }
    
    @Override public void setResponse(HttpServletResponse response)
        throws IOException {

        TemplateEngine templateEngine = new TemplateEngine();
        Map<String, Object> arguments = new HashMap<>();
        
        arguments.put("query", query.getRawString());

        final String escapedResult =
            StringEscapeUtils.escapeXml(result);
        
        // @todo We should not include HTML code here.
        if (fixedWidth) {
            arguments.put("result", "<pre>" + escapedResult + "</pre>");
        }
        else {
            arguments.put("result", escapedResult);
        }
        
        try {
            Template template =
                templateEngine.getTemplate("templates/single-query-result.html.ftl");
            response.addHeader("Content-Type", ContentType.HTML);            
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
