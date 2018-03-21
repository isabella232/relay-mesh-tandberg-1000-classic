package vc.bjn.catalyst.mesh.tandberg1000classic.dispatcher;

import org.mockito.*;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import vc.bjn.catalyst.mesh.tandberg1000classic.connection.TelnetConnection;
import vc.bjn.catalyst.mesh.tandberg1000classic.connection.TestEndpointTargetFactory;
import vc.bjn.catalyst.mesh.tandberg1000classic.data.Endpoint;
import vc.bjn.catalyst.mesh.tandberg1000classic.data.EndpointStatus;
import vc.bjn.catalyst.mesh.tandberg1000classic.data.Meeting;
import vc.bjn.catalyst.test.jersey.client.TestBuilder;

import javax.inject.Provider;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class Tandberg1000ClassicDispatcherTest {

    private Tandberg1000ClassicDispatcher dispatcher;
    private TestEndpointTargetFactory endpointTargetFactory;
    private Endpoint endpoint;

    @Captor private ArgumentCaptor<Entity<Form>> formCaptor;
    @Captor private ArgumentCaptor<Endpoint> endpointCaptor;
    @Mock private Provider<TelnetConnection> telnetConnectionProvider;
    @Mock private TelnetConnection telnetConnection;

    @BeforeMethod
    private void init() {
        MockitoAnnotations.initMocks(this);
        dispatcher = new Tandberg1000ClassicDispatcher();
        endpointTargetFactory = new TestEndpointTargetFactory();
        Whitebox.setInternalState(dispatcher, endpointTargetFactory);
        when(telnetConnectionProvider.get()).thenReturn(telnetConnection);
        Whitebox.setInternalState(dispatcher, telnetConnectionProvider);

        endpoint = new Endpoint();
        endpoint.setIpAddress("10.0.2.33");
        endpoint.setPassword("1adgjmptw");
    }

    @Test
    public void joinWithPasscode() {
        final TestBuilder dialRequest = endpointTargetFactory.addNewMockBuilder();
        when(dialRequest.post(any(Entity.class), eq(String.class)))
                .thenThrow(new RedirectionException("Document follows", 302, URI.create("/status.ssi")));

        final Meeting meeting = new Meeting();
        meeting.setMeetingId("111");
        meeting.setPasscode("0000");
        meeting.setDialString("111*0000@bjn.vc");
        meeting.setBridgeAddress("bjn.vc");

        dispatcher.join(endpoint, meeting);

        verify(dialRequest).requested("http://10.0.2.33:80/place_call");

        verify(dialRequest).post(formCaptor.capture(), eq(String.class));
        final MultivaluedMap<String, String> form = formCaptor.getValue().getEntity().asMap();
        assertEquals(form.size(), 4, "4 form parameters");
        assertEquals(form.get("number1"), Collections.singletonList("111*0000@bjn.vc"));
        assertEquals(form.get("defcall"), Collections.singletonList("Auto"));
        assertEquals(form.get("netwselind"), Collections.singletonList("1"));
        assertEquals(form.get("sub"), Collections.singletonList(""));
    }

    @Test
    public void joinWithoutPasscode() {
        final TestBuilder dialRequest = endpointTargetFactory.addNewMockBuilder();
        when(dialRequest.post(any(Entity.class), eq(String.class)))
                .thenThrow(new RedirectionException("Document follows", 302, URI.create("/status.ssi")));

        final Meeting meeting = new Meeting();
        meeting.setMeetingId("111");
        meeting.setPasscode(null);
        meeting.setDialString("111@bjn.vc");
        meeting.setBridgeAddress("bjn.vc");

        dispatcher.join(endpoint, meeting);

        verify(dialRequest).requested("http://10.0.2.33:80/place_call");

        verify(dialRequest).post(formCaptor.capture(), eq(String.class));
        final MultivaluedMap<String, String> form = formCaptor.getValue().getEntity().asMap();
        assertEquals(form.size(), 4, "4 form parameters");
        assertEquals(form.get("number1"), Collections.singletonList("111@bjn.vc"));
        assertEquals(form.get("defcall"), Collections.singletonList("Auto"));
        assertEquals(form.get("netwselind"), Collections.singletonList("1"));
        assertEquals(form.get("sub"), Collections.singletonList(""));
    }

    @Test
    public void hangUp() {
        final TestBuilder dialRequest = endpointTargetFactory.addNewMockBuilder();
        when(dialRequest.post(any(Entity.class), eq(String.class)))
                .thenThrow(new RedirectionException("Document follows", 302, URI.create("/status.ssi")));

        dispatcher.hangUp(endpoint);

        verify(dialRequest).requested("http://10.0.2.33:80/disconnect_call");

        verify(dialRequest).post(formCaptor.capture(), eq(String.class));
        final MultivaluedMap<String, String> form = formCaptor.getValue().getEntity().asMap();
        assertEquals(form.size(), 2, "2 form parameters");
        assertEquals(form.get("service"), Collections.singletonList("servvtlph"));
        assertEquals(form.get("line"), Collections.singletonList(""));
    }

    @Test
    public void getStatusCallActive() {
        final TestBuilder dialRequest = endpointTargetFactory.addNewMockBuilder();
        when(dialRequest.get(eq(String.class)))
                .thenReturn(readFixture("tandbergFixtures/statusCallActiveMuted.html"));

        final EndpointStatus actual = dispatcher.getStatus(endpoint);

        assertTrue(actual.isCallActive);

        verify(dialRequest).requested("http://10.0.2.33:80/status.ssi");
        verify(dialRequest).get(eq(String.class));
    }

    @Test
    public void getStatusCallInactive() {
        final TestBuilder dialRequest = endpointTargetFactory.addNewMockBuilder();
        when(dialRequest.get(eq(String.class)))
                .thenReturn(readFixture("tandbergFixtures/statusCallInactiveUnmuted.html"));

        final EndpointStatus actual = dispatcher.getStatus(endpoint);

        assertFalse(actual.isCallActive);

        verify(dialRequest).requested("http://10.0.2.33:80/status.ssi");
        verify(dialRequest).get(eq(String.class));
    }

    @Test
    public void getStatusMuted() {
        final TestBuilder dialRequest = endpointTargetFactory.addNewMockBuilder();
        when(dialRequest.get(eq(String.class)))
                .thenReturn(readFixture("tandbergFixtures/statusCallActiveMuted.html"));

        final EndpointStatus actual = dispatcher.getStatus(endpoint);

        assertTrue(actual.isMicrophoneMuted);

        verify(dialRequest).requested("http://10.0.2.33:80/status.ssi");
        verify(dialRequest).get(eq(String.class));
    }

    @Test
    public void getStatusUnmuted() {
        final TestBuilder dialRequest = endpointTargetFactory.addNewMockBuilder();
        when(dialRequest.get(eq(String.class)))
                .thenReturn(readFixture("tandbergFixtures/statusCallActiveUnmuted.html"));

        final EndpointStatus actual = dispatcher.getStatus(endpoint);

        assertFalse(actual.isMicrophoneMuted);

        verify(dialRequest).requested("http://10.0.2.33:80/status.ssi");
        verify(dialRequest).get(eq(String.class));
    }

    @Test
    public void muteMicrophoneWithPassword() {
        when(telnetConnection.open(any(Endpoint.class)))
                .thenReturn(readFixture("tandbergFixtures/telnetLogInPassword.txt"));

        when(telnetConnection.call(anyString()))
                .thenReturn("OK\n") //successful login
                .thenReturn("\nOK\n"); //successful mute

        dispatcher.setMicrophoneMute(endpoint, true);

        verify(telnetConnection).setMagicWait(eq(150));

        verify(telnetConnection).open(endpointCaptor.capture());
        final Endpoint actualEndpoint = endpointCaptor.getValue();
        assertEquals(actualEndpoint.getIpAddress(), "10.0.2.33");
        assertEquals((int) actualEndpoint.getPort(), 23);
        assertEquals(actualEndpoint.getPassword(), "1adgjmptw");

        final InOrder orderedVerification = inOrder(telnetConnection);
        orderedVerification.verify(telnetConnection).call("1adgjmptw");
        orderedVerification.verify(telnetConnection).call("mic off");

        verify(telnetConnection).close();

        verifyNoMoreInteractions(telnetConnection);
    }

    @Test
    public void unmuteMicrophoneWithoutPassword() {
        when(telnetConnection.open(any(Endpoint.class)))
                .thenReturn(readFixture("tandbergFixtures/telnetLogInAnonymous.txt"));

        when(telnetConnection.call(anyString())).thenReturn("\nOK\n");

        dispatcher.setMicrophoneMute(endpoint, false);

        verify(telnetConnection).setMagicWait(eq(150));

        verify(telnetConnection).open(endpointCaptor.capture());
        final Endpoint actualEndpoint = endpointCaptor.getValue();
        assertEquals(actualEndpoint.getIpAddress(), "10.0.2.33");
        assertEquals((int) actualEndpoint.getPort(), 23);
        assertEquals(actualEndpoint.getPassword(), "1adgjmptw");

        verify(telnetConnection).call("mic on");

        verify(telnetConnection).close();

        verifyNoMoreInteractions(telnetConnection);
    }

    private static String readFixture(final String path) {
        try {
            final URL resource = Tandberg1000ClassicDispatcherTest.class.getClassLoader().getResource(path);
            if (resource != null) {
                return new String(Files.readAllBytes(Paths.get(resource.toURI())));
            }
        } catch (final IOException | URISyntaxException e) {
            //throw below
        }
        throw new RuntimeException("Could not load text fixture file from src/test/resources/" + path);
    }
}