package com.argus;

/**
 * Manage MIME content types.
 */
public class ContentType {
    public static final String BINARY = "application/octet-stream";
    public static final String CSS = "text/css";
    public static final String HTML = "text/html; charset=utf-8";
    public static final String JAVASCRIPT =
        "application/javascript; charset=utf-8";
    public static final String PNG = "image/png";
    public static final String SVG = "image/svg+xml";
    public static final String XML = "application/xml";
    
    /** Return the MIME content type for the given file extension. */
    public static String fromFileExtension(final String extension) {
        if (extension == null) return BINARY;
        
        switch (extension) {
        case "css":
            return CSS;
        case "html":
            return HTML;
        case "js":
            return JAVASCRIPT;
        case "png":
            return PNG;
        case "svg":
            return SVG;
        case "xml":
            return XML;
        default:
            return BINARY;
        }
    }
}
