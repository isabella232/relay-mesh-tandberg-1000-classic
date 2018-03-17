package vc.bjn.catalyst.tandberg1000classicmesh;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import vc.bjn.catalyst.tandberg1000classicmesh.api.ObjectMapperProvider;

import javax.ws.rs.ProcessingException;
import java.net.URI;

public abstract class ServerStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerStarter.class);

    public static final URI BASE_URI = URI.create("http://0.0.0.0:6374/");

    private static boolean loggingInitialized = false;

    private ServerStarter() {
    }

    public static HttpServer startServer(final URI baseUri, final Binder binder) throws ProcessingException {
        initLogging();

        final ResourceConfig rc = new ResourceConfig()
                .packages(Main.class.getPackage().getName())
                .register(ObjectMapperProvider.class) //TODO maybe autodetect with @Provider?
                .register(JacksonFeature.class)
                .register(binder);

        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, rc);
        LOGGER.info("Listening on {}", baseUri);

        return server;
    }

    private static void initLogging() {
        if (!loggingInitialized) {
            loggingInitialized = true;
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
        }
    }
}