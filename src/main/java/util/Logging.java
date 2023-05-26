package util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class Logging {
    public static LoggerContext getContext() {
        return new LoggerContext("JournalDevLoggerContext");
    }

    public static Configuration getConfiguration(Class<?> cl) throws IOException {
        ConfigurationFactory factory = XmlConfigurationFactory.getInstance();
        URL configUrl = cl.getClassLoader().getResource("configuration.xml");
        InputStream inputStream = configUrl.openStream();
        ConfigurationSource configurationSource = new ConfigurationSource(inputStream);

        Configuration configuration = factory.getConfiguration(null, configurationSource);

        ConsoleAppender appender = ConsoleAppender
                .createDefaultAppenderForLayout(PatternLayout.createDefaultLayout());

        configuration.addAppender(appender);

        return configuration;
    }
}
