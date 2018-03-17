package vc.bjn.catalyst.tandberg1000classicmesh.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import vc.bjn.catalyst.tandberg1000classicmesh.data.Endpoint;
import vc.bjn.catalyst.tandberg1000classicmesh.data.EndpointStatus;
import vc.bjn.catalyst.tandberg1000classicmesh.data.Meeting;
import vc.bjn.catalyst.tandberg1000classicmesh.dispatcher.Dispatcher;

import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Listeners({TestServer.class})
public class EndpointResourceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointResourceTest.class);

    private static WebTarget target;
    private static Dispatcher dispatcher;

    private Endpoint endpoint;

    @Captor private ArgumentCaptor<Endpoint> endpointCaptor;
    @Captor private ArgumentCaptor<Meeting> meetingCaptor;

    @BeforeClass
    private void init() {
        MockitoAnnotations.initMocks(this);

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        final JacksonJaxbJsonProvider jacksonJaxbJsonProvider = new JacksonJaxbJsonProvider(objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);

        final java.util.logging.Logger httpLogger = java.util.logging.Logger.getLogger("http-client");
        httpLogger.setLevel(Level.ALL);
        final LoggingFeature loggingFeature = new LoggingFeature(httpLogger, LoggingFeature.Verbosity.PAYLOAD_ANY);
        final ClientConfig clientConfig = new ClientConfig(jacksonJaxbJsonProvider, loggingFeature);

        final Client client = ClientBuilder.newClient(clientConfig);

        target = client.target(TestServer.getBaseUri())
                .register(HttpAuthenticationFeature.basic("swordfish", "txburocks"))
                .register(new ClientRequestFilter() {
                    @Override
                    public void filter(final ClientRequestContext request) {
                        if (!request.getHeaders().containsKey("X-bjn-mesh-version")) {
                            request.getHeaders().add("X-bjn-mesh-version", "1");
                        }
                    }
                });

        dispatcher = TestServer.getService(Dispatcher.class);

        endpoint = new Endpoint();
        endpoint.setName("Interocitor");
        endpoint.setIpAddress("10.0.2.33");
        endpoint.setListenerServiceId("527ca8e2776a7e02dd2e15fe");
        endpoint.setControlProtocol("MESH");
    }

    @Test
    public void getCapabilities() {
        final Map<String, Boolean> actual = target.path("10.0.2.33")
                .path("capabilities")
                .queryParam("port", 443)
                .queryParam("name", "Interocitor")
                .queryParam("customData.intensifier", "11")
                .request()
                .get(new GenericType<Map<String, Boolean>>() {
                });

        final Map<String, Boolean> expected = new HashMap<>();
        expected.put("JOIN", true);
        expected.put("HANGUP", true);
        expected.put("STATUS", true);
        expected.put("MUTE_MICROPHONE", true);
        expected.put("CALENDAR_PUSH", false);

        assertEquals(actual, expected);
    }

    @Test
    public void getStatus() {
        final EndpointStatus status = new EndpointStatus();
        status.isCallActive = true;
        status.isMicrophoneMuted = false;
        when(dispatcher.getStatus(any(Endpoint.class))).thenReturn(status);

        final Map<String, Boolean> response = target.path("10.0.2.33")
                .path("status")
                .queryParam("port", 443)
                .queryParam("name", "Interocitor")
                .queryParam("customData.intensifier", "11")
                .request()
                .get(new GenericType<Map<String, Boolean>>() {
                });

        final Map<String, Boolean> expected = new HashMap<>();
        expected.put("callActive", status.isCallActive);
        expected.put("microphoneMuted", status.isMicrophoneMuted);

        assertEquals(response, expected);

        verify(dispatcher).getStatus(endpointCaptor.capture());
        final Endpoint actual = endpointCaptor.getValue();
        assertNotNull(actual.getPort(), "port");
        assertEquals((int) actual.getPort(), 443, "port");
        assertEquals(actual.getName(), "Interocitor");
    }

    @Test
    public void join() {
        final Response actual = target.path("10.0.2.33")
                .path("join")
                .queryParam("dialString", "111.0000@bjn.vc")
                .queryParam("meetingId", "111")
                .queryParam("passcode", "0000")
                .queryParam("bridgeAddress", "bjn.vc")
                .request()
                .post(Entity.json(endpoint));

        assertEquals(actual.getStatus(), Response.Status.NO_CONTENT.getStatusCode());

        verify(dispatcher).join(endpointCaptor.capture(), meetingCaptor.capture());
        final Endpoint actualEndpoint = endpointCaptor.getValue();
        assertEquals(actualEndpoint.getName(), "Interocitor");
        assertEquals(actualEndpoint.getIpAddress(), "10.0.2.33");
        assertEquals(actualEndpoint.getListenerServiceId(), "527ca8e2776a7e02dd2e15fe");
        assertEquals(actualEndpoint.getControlProtocol(), "MESH");
        final Meeting actualMeeting = meetingCaptor.getValue();
        assertEquals(actualMeeting.getDialString(), "111.0000@bjn.vc");
        assertEquals(actualMeeting.getMeetingId(), "111");
        assertEquals(actualMeeting.getPasscode(), "0000");
        assertEquals(actualMeeting.getBridgeAddress(), "bjn.vc");
    }

    @Test
    public void hangUp() {
        final Response actual = target.path("10.0.2.33")
                .path("hangup")
                .request()
                .post(Entity.json(endpoint));

        assertEquals(actual.getStatus(), Response.Status.NO_CONTENT.getStatusCode());

        verify(dispatcher).hangUp(endpointCaptor.capture());
        final Endpoint actualEndpoint = endpointCaptor.getValue();
        assertEquals(actualEndpoint.getName(), "Interocitor");
        assertEquals(actualEndpoint.getIpAddress(), "10.0.2.33");
        assertEquals(actualEndpoint.getListenerServiceId(), "527ca8e2776a7e02dd2e15fe");
        assertEquals(actualEndpoint.getControlProtocol(), "MESH");
    }

    @Test
    public void muteMicrophone() {
        final Response actual = target.path("10.0.2.33")
                .path("mutemicrophone")
                .request()
                .post(Entity.json(endpoint));

        assertEquals(actual.getStatus(), Response.Status.NO_CONTENT.getStatusCode());

        verify(dispatcher).setMicrophoneMute(endpointCaptor.capture(), eq(true));
        final Endpoint actualEndpoint = endpointCaptor.getValue();
        assertEquals(actualEndpoint.getName(), "Interocitor");
        assertEquals(actualEndpoint.getIpAddress(), "10.0.2.33");
        assertEquals(actualEndpoint.getListenerServiceId(), "527ca8e2776a7e02dd2e15fe");
        assertEquals(actualEndpoint.getControlProtocol(), "MESH");
    }

    @Test
    public void unmuteMicrophone() {
        final Response actual = target.path("10.0.2.33")
                .path("unmutemicrophone")
                .request()
                .post(Entity.json(endpoint));

        assertEquals(actual.getStatus(), Response.Status.NO_CONTENT.getStatusCode());

        verify(dispatcher).setMicrophoneMute(endpointCaptor.capture(), eq(false));
        final Endpoint actualEndpoint = endpointCaptor.getValue();
        assertEquals(actualEndpoint.getName(), "Interocitor");
        assertEquals(actualEndpoint.getIpAddress(), "10.0.2.33");
        assertEquals(actualEndpoint.getListenerServiceId(), "527ca8e2776a7e02dd2e15fe");
        assertEquals(actualEndpoint.getControlProtocol(), "MESH");
    }
}
