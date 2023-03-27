////////////////////////////////////////////////////////////////////////////////
package com.argus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

/**
 * Class to manage admin settings.
 */
public class Settings {

    // @todo Add support for Windows file paths.
    private static String FILE_NAME = "/argus/settings.yaml";
    
    private static final Logger LOGGER =
        Logger.getLogger(Settings.class.getName());

    private static Settings INSTANCE = new Settings();

    private List<String> searchEngines;
    private String defaultSearchEngine;
    private Map<String, Object> bangs;

    private Settings() {
        // First try to read user's settings. If not written then
        // read default settings.
        try {
            try {
                FileInputStream stream = new FileInputStream(FILE_NAME);
                read(stream);
            }
            catch (FileNotFoundException exception) {
                InputStream stream = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("default_settings.yaml");
                read(stream);
            }
        }
        catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Error reading settings",
                       exception);
        }
    }

    private void read(InputStream stream) throws FileNotFoundException {
        Yaml yaml = new Yaml();        
        Map<String, Object> settings = yaml.load(stream);

        // @todo Validate file.

        searchEngines = ((List<Object>)settings.get("search_engines"))
            .stream()
            .map(searchEngine -> (String)searchEngine)
            .collect(Collectors.toList());            
        defaultSearchEngine = (String)settings.get("default_search_engine");

        bangs = (Map<String, Object>)settings.get("bangs");
    }
    
    public static Settings getInstance() {
        return INSTANCE;
    }

    public Map<String, Object> getBangs() {
        return bangs;
    }    

    public Map<String, Object> getBang(final String id) {
        return (Map<String, Object>)bangs.get(id);
    }

    public String getBangName(final String id) {
        return (String)getBang(id).get("name");
    }

    public String getBangUrl(final String id) {
        return (String)getBang(id).get("url");
    }
    
    public void setDefaultSearchEngine(final String defaultSearchEngine) {
        this.defaultSearchEngine = defaultSearchEngine;
    }

    public String getDefaultSearchEngine() {
        return defaultSearchEngine;
    }
    
    public String getDefaultSearchEngineUrl() {
        return getBangUrl(defaultSearchEngine);
    }
    
    public void save() {
        try {
            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(FILE_NAME);
            
            Map<String, Object> settings = new HashMap<>();
            settings.put("search_engines", searchEngines);
            settings.put("default_search_engine", defaultSearchEngine);
            settings.put("bangs", bangs);
            
            yaml.dump(settings, writer);
        }
        catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Error writing settings file.",
                       exception);
        }        
    }

    public List<String> getSearchEngines() {
        return searchEngines;
    }
}
