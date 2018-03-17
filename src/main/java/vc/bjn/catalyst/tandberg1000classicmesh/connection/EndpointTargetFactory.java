package vc.bjn.catalyst.tandberg1000classicmesh.connection;

import vc.bjn.catalyst.tandberg1000classicmesh.data.Endpoint;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.net.URI;

public interface EndpointTargetFactory {
    WebTarget forEndpoint(Client client, Endpoint endpoint);

    WebTarget getTarget(Client client, URI uri);

    WebTarget configureAuth(Endpoint endpoint, WebTarget target);
}
