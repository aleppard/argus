////////////////////////////////////////////////////////////////////////////////
package com.argus;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Class to represent settings received from a user by the API. */
public class InputSettings
{
    @JsonProperty("default_search_engine")
    public String defaultSearchEngine;

    public InputSettings() {};
}
