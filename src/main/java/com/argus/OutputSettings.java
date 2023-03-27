////////////////////////////////////////////////////////////////////////////////
package com.argus;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Class to represent settings returned to a user by the API. */
public class OutputSettings
{
    public static class SearchEngine {
        @JsonProperty
        public String id;
        
        @JsonProperty
        public String name;

        public SearchEngine(final String id, final String name) {
            this.id = id;
            this.name = name;
        }
    }
    
    @JsonProperty("search_engines")
    public List<SearchEngine> searchEngines;
    
    @JsonProperty("default_search_engine")
    public String defaultSearchEngine;

    public OutputSettings() {};
}
