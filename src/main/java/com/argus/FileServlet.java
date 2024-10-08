////////////////////////////////////////////////////////////////////////////////
package com.argus;

import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;

import freemarker.template.Template;

import org.springframework.core.io.ClassPathResource;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * Servlet to access public front-end files.
 */
@RestController
public class FileServlet
{
    private static final Logger LOGGER =
        Logger.getLogger(FileServlet.class.getName());

    private TemplateEngine templateEngine = new TemplateEngine();

    @GetMapping({"*", "js/*"})
    public void get(final HttpServletRequest request,
                    final HttpServletResponse response)
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
                Template template = templateEngine.getTemplate("/web" + uri);
                template.process(arguments, response.getWriter());
                response.setStatus(HttpServletResponse.SC_OK);
            }
            catch (Exception exception) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        else if (uri.equals("/settings")) {
            if (Authentication.isAuthorisedForAdminAccess(request)) {
                // React web pages. Currently only /settings.
                InputStream input =
                    new ClassPathResource("/web/app.html")
                    .getInputStream();
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
            final ClassPathResource resource =
                new ClassPathResource("/web" + uri);
            if (resource.exists()) {
                InputStream input = resource.getInputStream();
                ByteStreams.copy(input, response.getOutputStream());

                // Cache the landing page which should change very infrequently.
                // One of the features of Argus is speed so let the browser cache
                // this page to avoid delays.
                if (uri.equals("/index.html")) {
                    final int maxAgeInSeconds = 60*60*24; // 1 day
                    response.addHeader("Cache-Control",
                                       "max-age=" + maxAgeInSeconds + ", public");
                }
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                LOGGER.info("404 " + uri);
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
        // If the x-forwarded-host header is set then we use that.
        // This way we can work behind a proxy and return the host URL.
        String url;
        final String xForwardedHost = request.getHeader("x-forwarded-host");
        if (xForwardedHost != null) {
            // @todo How can well tell whether it should be http(s)?
            url = "https://" + xForwardedHost + "/";
        }
        else {
            url = request.getRequestURL().toString();
            final int lastSlashIndex = url.lastIndexOf('/');
            url = url.substring(0, lastSlashIndex + 1);
        }

        return url;
    }
}
