package vc.bjn.catalyst.tandberg1000classicmesh;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import vc.bjn.catalyst.tandberg1000classicmesh.api.ObjectMapperProvider;
import vc.bjn.catalyst.tandberg1000classicmesh.connection.EndpointTargetFactory;
import vc.bjn.catalyst.tandberg1000classicmesh.connection.EndpointTargetFactoryImpl;
import vc.bjn.catalyst.tandberg1000classicmesh.dispatcher.Dispatcher;
import vc.bjn.catalyst.tandberg1000classicmesh.dispatcher.Tandberg1000ClassicDispatcher;

import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * @see TestBinder for the services that get injected while testing
 */
public class MainBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(Tandberg1000ClassicDispatcher.class).to(Dispatcher.class)
                .in(Singleton.class); //TODO can we autodetect this?

        bindFactory(new ClientFactory()).to(Client.class).in(Singleton.class); //might leak memory

        bind(EndpointTargetFactoryImpl.class).to(EndpointTargetFactory.class).in(Singleton.class);
    }

    private static final class ClientFactory implements Factory<Client> {

        @Override
        public Client provide() {
            final ClientConfig config = new ClientConfig();

            config.property(ClientProperties.CONNECT_TIMEOUT, 5000);
            config.property(ClientProperties.READ_TIMEOUT, 5000);
            config.property(ClientProperties.FOLLOW_REDIRECTS, false);

            config.register(ObjectMapperProvider.class);
            config.register(JacksonJaxbJsonProvider.class);

            return ClientBuilder.newClient(config);
        }

        @Override
        public void dispose(final Client client) {
            client.close();
        }
    }
}
