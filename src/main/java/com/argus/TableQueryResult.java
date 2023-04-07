////////////////////////////////////////////////////////////////////////////////
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

import org.apache.commons.lang3.StringEscapeUtils;

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
        private boolean escape = true;

        public Cell(final String text) {
            this.text = text;
        }

        public Cell(final String text, boolean fixedWidth) {
            this.text = text;
            this.fixedWidth = fixedWidth;
        }

        // @todo Remove these values from the constructor and add set
        // methods.
        public Cell(final String text, boolean fixedWidth, boolean escape) {
            this.text = text;
            this.fixedWidth = fixedWidth;
            this.escape = escape;
        }                

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();

            final String escapedText =
                escape? StringEscapeUtils.escapeXml(text) : text;
            
            // @todo We should not include HTML code here.
            if (fixedWidth) {
                map.put("text", "<pre>" + escapedText + "</pre>");
            }
            else {
                map.put("text", escapedText);
            }

            return map;
        }
    }
   
    private Query query;
    private List<List<Map<String, Object>>> table = new ArrayList<>();

    /**
     * @param query the user's query.
     */
    public TableQueryResult(final Query query) {
        this.query = query;
    }

    /** Add a row of results to the table. */
    public void addRow(final Cell... cells) {
        table.add(Arrays.asList(cells)
                  .stream()
                  .map(cell -> cell.toMap())
                  .collect(Collectors.toList()));
    }

    public void addRow(final String... cells) {
        table.add(Arrays.asList(cells)
                  .stream()
                  .map(cell -> new Cell(cell).toMap())
                  .collect(Collectors.toList()));
    }

    public String getCellText(int row, int column) {
        return (String)table.get(row).get(column).get("text");
    }
    
    public int getRowCount() {
        return table.size();
    }
    
    @Override public void setResponse(HttpServletResponse response)
        throws IOException {

        TemplateEngine templateEngine = new TemplateEngine();
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("query", query.getRawString());
        arguments.put("table", table);

        try {
            Template template =
                templateEngine.getTemplate("templates/table-query-result.html.ftl");
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
