package vc.bjn.catalyst.tandberg1000classicmesh;

import org.glassfish.grizzly.http.server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) throws IOException {
//        System.setProperty("http.proxyHost", "127.0.0.1");
//        System.setProperty("https.proxyHost", "127.0.0.1");
//        System.setProperty("http.proxyPort", "9998");
//        System.setProperty("https.proxyPort", "9998");

        final HttpServer server = ServerStarter.startServer(ServerStarter.BASE_URI, new MainBinder());
        LOGGER.info("Press Enter to exit.");
        System.in.read();
        server.shutdownNow();
    }
}
