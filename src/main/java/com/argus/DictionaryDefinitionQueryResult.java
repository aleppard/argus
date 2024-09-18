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

import com.argus.resolver.DictionaryEntry;

/**
 * Representation of a query result that consists of a table of
 * results.
 */
// MYTODO
public class DictionaryDefinitionQueryResult implements QueryResult {
    
    private static final Logger LOGGER =
        Logger.getLogger(DictionaryDefinitionQueryResult.class.getName());

    private Query query;
    private String word;
    private List<DictionaryEntry> entries;
    
    public DictionaryDefinitionQueryResult
        (final Query query,
         final String word,
         final List<DictionaryEntry> entries) {
        this.query = query;
        this.word = word;
        this.entries = entries;
    }

    private List<Map<String, Object>> convert(List<DictionaryEntry> entries) {
        // Group definitions by their part-of-speech.
        Map<String, List<String>> partOfSpeechDefinitions = new
            HashMap<>();
        for (final DictionaryEntry entry: entries) {
            partOfSpeechDefinitions.computeIfAbsent
                (entry.partOfSpeech,
                 k -> new ArrayList<>()).add(entry.definition);
        }

        List<Map<String, Object>> mapEntries = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry :
                 partOfSpeechDefinitions.entrySet()) {
            Map<String, Object> mapEntry = new HashMap<>();
            mapEntry.put("partOfSpeech", entry.getKey());
            mapEntry.put("definitions", entry.getValue());
            mapEntries.add(mapEntry);
        }
        
        return mapEntries;
    }
    
    @Override public void setResponse(HttpServletResponse response)
        throws IOException {

        TemplateEngine templateEngine = new TemplateEngine();
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("query", query.getRawString());
        arguments.put("word", word);
        arguments.put("entries", convert(entries));

        try {
            Template template =
                templateEngine.getTemplate
                ("templates/dictionary-definition-query-result.html.ftl");
            response.addHeader("Content-Type", ContentType.HTML);            
            template.process(arguments, response.getWriter());
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception exception) {
            LOGGER.log(Level.SEVERE,
                       "Error returning dictionary definition query result.",
                       exception);            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
     }
}
