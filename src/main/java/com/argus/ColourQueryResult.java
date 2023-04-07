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
 * Representation of a colour returned to the user as part of a
 * query.
 */
public class ColourQueryResult implements QueryResult {

    private static final Logger LOGGER =
        Logger.getLogger(ColourQueryResult.class.getName());

    private Query query;
    private int red;
    private int green;
    private int blue;

    private List<List<Map<String, Object>>> table = new ArrayList<>();

    /**
     * @param query the user's query.
     */
    public ColourQueryResult(final Query query, int red, int green, int blue) {
        this.query = query;
        this.red = red;
        this.green = green;
        this.blue = blue;

        // @todo Add HTML colour name if it matches.
        addRow("CSS Hex", "#" +
               String.format("%02X", red) +
               String.format("%02X", green) +
               String.format("%02X", blue));
        addRow("CSS RGB", "rgb(" + red + ", " + green + ", " + blue + ")"); 
    }

    private void addRow(final String... cells) {
        table.add(Arrays.asList(cells)
                  .stream()
                  .map(cell -> new TableQueryResult.Cell(cell).toMap())
                  .collect(Collectors.toList()));
    }
    
    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }
    
    @Override public void setResponse(HttpServletResponse response)
        throws IOException {

        TemplateEngine templateEngine = new TemplateEngine();
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("query", query.getRawString());
        arguments.put("colour",
                      "rgb(" + red + ", " + green + ", " + blue + ")");

        // @todo Change colour of writing depending on background colour
        // so that it's always legible.
        arguments.put("table", table);

        try {
            Template template =
                templateEngine.getTemplate("templates/colour-query-result.html.ftl");
            response.addHeader("Content-Type", ContentType.HTML);
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
