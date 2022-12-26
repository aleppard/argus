package com.argus;

import java.util.logging.Level;
import java.util.logging.Logger;

import freemarker.core.PlainTextOutputFormat;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

import no.api.freemarker.java8.Java8ObjectWrapper;

/**
 * Class to configure the template engine used.
 */
public class TemplateEngine {

    private static final Logger LOGGER =
        Logger.getLogger(TemplateEngine.class.getName());
    
    private Configuration xmlConfiguration;

    private final static Version FREEMARKER_VERSION =
        Configuration.VERSION_2_3_29;
    
    public TemplateEngine() {
        // Set up Freemarker XML template configuration.
        xmlConfiguration = new Configuration(FREEMARKER_VERSION);
        xmlConfiguration.setDefaultEncoding("UTF-8");
        xmlConfiguration.setClassForTemplateLoading
            (TemplateEngine.class, "/");
        xmlConfiguration.setTemplateExceptionHandler
            (TemplateExceptionHandler.RETHROW_HANDLER);
        xmlConfiguration.setOutputFormat(PlainTextOutputFormat.INSTANCE);
        
        Java8ObjectWrapper wrapper =
            new Java8ObjectWrapper(FREEMARKER_VERSION);
        wrapper.setExposeFields(true);
        xmlConfiguration.setObjectWrapper(wrapper);
    }

    public Template getTemplate(final String templateName) {
        try {
            return xmlConfiguration.getTemplate(templateName);
        }
        catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Error with template " + templateName,
                       exception); 
            return null;
        }
    }
}
