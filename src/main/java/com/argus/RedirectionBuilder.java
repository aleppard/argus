package com.argus;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

/**
 * Build and return a HTML that redirects to a given URL.
 */
public class RedirectionBuilder
{
    private String url;
    private String query;
    
    public RedirectionBuilder(final String url, final String query) {
        this.url = url;
        this.query = query;
    }

    public String build() throws UnsupportedEncodingException {
        // @todo Store this in a template file.
        return
            "<head>" +
            "<style>" +
            "body {" +
            "background-color: #171717;" +
            "}" +
            "</style>" +
            "<meta http-equiv=\"Refresh\" content=\"0; URL=" +
            url +
            URLEncoder.encode(query, "UTF-8") +
            "\"/>" +
            "</head>";
    }
}
