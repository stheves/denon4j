package io.theves.denon4j.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * Logging system for denon4j API.
 *
 * @author stheves
 */
public class LoggingSystem {
    public void initialize() {
        InputStream in = LoggingSystem.class.getResourceAsStream("/io/theves/denon4j/logging/logging.properties");
        if (null == in) {
            // should not happen
            throw new IllegalStateException("Could not find 'logging.properties'." +
                " You may configure one with -Djava.utli.logging.config.file=/path/to/log.properties.");
        }
        try {
            LogManager.getLogManager().readConfiguration(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize logging system.", e);
        }
    }
}
