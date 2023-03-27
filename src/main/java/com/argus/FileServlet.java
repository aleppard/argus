////////////////////////////////////////////////////////////////////////////////
package com.argus;

import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;

import freemarker.template.Template;

/**
 * Servlet to access public front-end files.
 */
public class FileServlet extends HttpServlet
{
    private static final Logger LOGGER =
        Logger.getLogger(FileServlet.class.getName());

    private TemplateEngine templateEngine = new TemplateEngine();
    
    @Override public void doGet(HttpServletRequest request,
                                HttpServletResponse response)
        throws IOException {
        
        // Ignore /argus/ prefix if present.
        final String prefix = "/argus";
        String uri = request.getRequestURI();
        if (uri.startsWith(prefix)) {
            uri = uri.substring(prefix.length());
        }
        
        if (uri.length() == 1) {
            uri = "/index.html";
        }
        
        // @todo Refactor this.
        if (uri.equals("/opensearch.xml")) {
            // The opensearch.xml file needs to be updated with the
            // actual URL of Argus.
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("base_url", getBaseUrl(request));
            
            try {
                Template template = templateEngine.getTemplate("web/" + uri);
                template.process(arguments, response.getWriter());
                response.setStatus(HttpServletResponse.SC_OK);
            }
            catch (Exception exception) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        else if (uri.equals("/settings")) {
            if (Authentication.isAuthorisedForAdminAccess(request)) {
                // React web pages. Currenty only /settings.
                InputStream input =
                    FileServlet.class.getClassLoader().getResourceAsStream
                    ("web/app.html");
                ByteStreams.copy(input, response.getOutputStream());
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                response.addHeader("WWW-Authenticate",
                                   "Basic realm=\"Settings\"");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        else {
            final String extension = getFileExtension(uri);
            response.setContentType
                (ContentType.fromFileExtension(extension));
            InputStream input =
                FileServlet.class.getClassLoader().getResourceAsStream
                ("web/" + uri);
            if (input != null) {
                ByteStreams.copy(input, response.getOutputStream());
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    /** Given a file name, returns its file extension. */
    private String getFileExtension(final String uri) {
        final int lastDotIndex = uri.lastIndexOf('.');
        if (lastDotIndex < 0) return null;

        return uri.substring(lastDotIndex + 1);
    }

    /** Get the base URL for this server. */
    private String getBaseUrl(final HttpServletRequest request) {
        final String url = request.getRequestURL().toString();
        final int lastSlashIndex = url.lastIndexOf('/');

        return url.substring(0, lastSlashIndex + 1);
    }
}
