////////////////////////////////////////////////////////////////////////////////
package com.argus;

import java.io.IOException;

import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

/**
 * An API that lets the user retrieve or set admin settings.
 */
public class SettingsServlet extends HttpServlet
{
    private static final Logger LOGGER =
        Logger.getLogger(SettingsServlet.class.getName());

    @Override public void doGet(HttpServletRequest request,
                                HttpServletResponse response)
        throws IOException {
        
        response.setContentType("application/json; charset=utf-8");
        
        if (!Authentication.isAuthorisedForAdminAccess(request)) {
            response.addHeader("WWW-Authenticate",
                               "Basic realm=\"Settings\"");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        OutputSettings settings = new OutputSettings();

        settings.defaultSearchEngine =
            Settings.getInstance().getDefaultSearchEngine();

        final List<String> searchEngineIds =
            Settings.getInstance().getSearchEngines();
        settings.searchEngines =
            searchEngineIds.
            stream().
            map(id ->
                new OutputSettings.SearchEngine
                (id,
                 Settings.getInstance().getBangName(id)))
            .collect(Collectors.toList());
        
        mapper.writeValue(response.getWriter(), settings);        
    }
    
    @Override public void doPut(HttpServletRequest request,
                                HttpServletResponse response)
        throws IOException {

        response.setContentType("application/json; charset=utf-8");
        
        if (!Authentication.isAuthorisedForAdminAccess(request)) {
            response.addHeader("WWW-Authenticate",
                               "Basic realm=\"Settings\"");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();        
        InputSettings settings =
            mapper.readValue(request.getReader(), InputSettings.class);

        // @todo Validate the default serach engine.
        Settings.getInstance().setDefaultSearchEngine
            (settings.defaultSearchEngine);
        Settings.getInstance().save();
        
        response.getWriter().print(new JSONObject().toString());
    }
}

