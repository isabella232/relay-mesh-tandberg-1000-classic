package vc.bjn.catalyst.tandberg1000classicmesh.connection;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import vc.bjn.catalyst.tandberg1000classicmesh.data.Endpoint;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.net.URI;
import java.net.URISyntaxException;

public class EndpointTargetFactoryImpl implements EndpointTargetFactory {

    @Override
    public WebTarget forEndpoint(final Client client, final Endpoint endpoint) {
        Integer port = endpoint.getPort();
        if (port == null) {
            port = 80;
        }

        try {
            final String scheme = (port == 443 || port == 8443) ? "https" : "http";
            final URI uri = new URI(scheme, null, endpoint.getIpAddress(), port, null, null, null);
            WebTarget target = getTarget(client, uri);
            target = configureAuth(endpoint, target);
            return target;
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WebTarget getTarget(final Client client, final URI uri) {
        return client.target(uri);
    }

    @Override
    public WebTarget configureAuth(final Endpoint endpoint, final WebTarget target) {
        String username = endpoint.getUsername();
        if (username == null || username.isEmpty()) {
            /* Jersey's Digest auth filter doesn't work with empty username (it omits the leading colon from HA1).
             * We can't fall back to Basic, because the Tandberg 1000 Classic only supports Digest.
             * To work around this, send the fake username "admin", which the endpoint will happily accept.
             * See org.glassfish.jersey.client.authentication.DigestAuthenticator.md5(String...) line 347
             */
            username = "admin";
        }
        return target.register(HttpAuthenticationFeature.digest(username, endpoint.getPassword()));
    }

}
