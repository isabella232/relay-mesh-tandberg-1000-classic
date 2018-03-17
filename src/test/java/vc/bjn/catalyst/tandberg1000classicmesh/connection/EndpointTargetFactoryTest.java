package vc.bjn.catalyst.tandberg1000classicmesh.connection;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import vc.bjn.catalyst.tandberg1000classicmesh.data.Endpoint;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.testng.Assert.*;

public class EndpointTargetFactoryTest {

    private EndpointTargetFactoryImpl endpointTargetFactory;

    private Client httpClient;
    private Endpoint endpoint;

    @BeforeMethod
    private void init() {
        endpointTargetFactory = new EndpointTargetFactoryImpl();

        endpoint = new Endpoint();
        endpoint.setIpAddress("10.0.2.33");
        endpoint.setUsername(null);
        endpoint.setPassword("txburocks");

        httpClient = JerseyClientBuilder.createClient();
    }

    @AfterMethod
    private void tearDown() {
        httpClient.close();
    }

    @Test
    public void tandberg1000ClassicAuth() {
        final WebTarget target = endpointTargetFactory.forEndpoint(httpClient, endpoint);

        final HttpAuthenticationFeature authFeature = getHttpAuthenticationFeature(target);
        assertNotNull(authFeature);

        assertEquals(getMode(authFeature).name(), "DIGEST", "mode");
        final Object digestCredentials = getDigestCredentials(authFeature);
        final String username = getUsername(digestCredentials);
        final String password = getPassword(digestCredentials);

        assertEquals(username, "admin", "username");
        assertEquals(password, "txburocks", "password");
    }

    private static HttpAuthenticationFeature getHttpAuthenticationFeature(final WebTarget target) {
        final Set<Object> registrations = target.getConfiguration().getInstances();

        HttpAuthenticationFeature authFeature = null;
        for (final Object registration : registrations) {
            if (registration instanceof HttpAuthenticationFeature) {
                authFeature = (HttpAuthenticationFeature) registration;
                break;
            }
        }
        return authFeature;
    }

    private static Enum<?> getMode(final HttpAuthenticationFeature authFeature) {
        return Whitebox.getInternalState(authFeature, "mode");
    }

    private static Object getDigestCredentials(final HttpAuthenticationFeature authFeature) {
        return Whitebox.getInternalState(authFeature, "digestCredentials");
    }

    private static String getUsername(final Object credentials) {
        return Whitebox.getInternalState(credentials, "username");
    }

    private static String getPassword(final Object credentials) {
        return new String((byte[]) Whitebox.getInternalState(credentials, "password"), StandardCharsets.ISO_8859_1);
    }
}
