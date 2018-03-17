package vc.bjn.catalyst.tandberg1000classicmesh;

import org.glassfish.jersey.logging.LoggingFeature;

import java.util.logging.Level;

public abstract class JerseyToSLF4JLogger {
    private JerseyToSLF4JLogger() {
    }

    public static LoggingFeature getLoggingFeature(final String name) {
        final java.util.logging.Logger httpLogger = java.util.logging.Logger.getLogger(name);
        httpLogger.setLevel(Level.ALL);
        return new LoggingFeature(httpLogger, LoggingFeature.Verbosity.PAYLOAD_ANY);
    }
}
