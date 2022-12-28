package com.argus;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import freemarker.template.Template;

/**
 * Representation of a query result that consists of a table of
 * results.
 */
public class TableQueryResult implements QueryResult {
    
    private static final Logger LOGGER =
        Logger.getLogger(TableQueryResult.class.getName());

    public static class Cell {
        private String text;
        private boolean fixedWidth = false;

        public Cell(final String text) {
            this.text = text;
        }

        public Cell(final String text, boolean fixedWidth) {
            this.text = text;
            this.fixedWidth = fixedWidth;
        }        

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            // @todo We should not include HTML code here.
            if (fixedWidth) {
                map.put("text", "<pre>" + text + "</pre>");
            }
            else {
                map.put("text", text);
            }

            return map;
        }
    }
   
    private String query;
    private List<List<Map<String, Object>>> table = new ArrayList<>();

    /**
     * @param query the user's query.
     */
    public TableQueryResult(final String query) {
        this.query = query;
    }

    /** Add a row of results to the table. */
    public void addRow(final Cell... cells) {
        table.add(Arrays.asList(cells)
                  .stream()
                  .map(cell -> cell.toMap())
                  .collect(Collectors.toList()));
    }

    @Override public void setResponse(HttpServletResponse response)
        throws IOException {

        TemplateEngine templateEngine = new TemplateEngine();
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("query", query);
        arguments.put("table", table);

        try {
            Template template =
                templateEngine.getTemplate("templates/table-query-result.html");
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
