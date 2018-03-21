package vc.bjn.catalyst.mesh.tandberg1000classic.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vc.bjn.catalyst.mesh.tandberg1000classic.connection.EndpointTargetFactory;
import vc.bjn.catalyst.mesh.tandberg1000classic.connection.TelnetConnection;
import vc.bjn.catalyst.mesh.tandberg1000classic.data.Endpoint;
import vc.bjn.catalyst.mesh.tandberg1000classic.data.EndpointStatus;
import vc.bjn.catalyst.mesh.tandberg1000classic.data.Meeting;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tandberg1000ClassicDispatcher implements Dispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tandberg1000ClassicDispatcher.class);

    private static final Pattern IS_MICROPHONE_MUTED = Pattern.compile(".*Microphone:.*?(On|Off).*", Pattern.DOTALL);

    @Inject private EndpointTargetFactory endpointTargetFactory;
    @Inject private Client client;
    @Inject private Provider<TelnetConnection> telnetConnectionProvider;

    @Override
    public EndpointStatus getStatus(final Endpoint endpoint) {
        final String statusPage = endpointTargetFactory.forEndpoint(client, endpoint)
                .path("status.ssi")
                .request()
                .get(String.class);

        final EndpointStatus status = new EndpointStatus();
        status.isCallActive = !statusPage.contains("disconnected");

        final Matcher matcher = IS_MICROPHONE_MUTED.matcher(statusPage);
        status.isMicrophoneMuted = matcher.matches() && "Off".equals(matcher.group(1));

        return status;
    }

    @Override
    public void join(final Endpoint endpoint, final Meeting meeting) {
        try {
            final Form params = new Form();
            params.param("number1", meeting.getDialString());
            params.param("defcall", "Auto");
            params.param("netwselind", "1");
            params.param("sub", "");

            endpointTargetFactory.forEndpoint(client, endpoint)
                    .path("place_call")
                    .request()
                    .post(Entity.form(params), String.class);
        } catch (final RedirectionException e) {
            //this indicates a successful response from the endpoint
        } catch (final WebApplicationException e) {
            LOGGER.error("Endpoint returned status {}: {}", e.getResponse().getStatus(), e.getMessage());
        }
    }

    @Override
    public void hangUp(final Endpoint endpoint) {
        try {
            final Form params = new Form();
            params.param("service", "servvtlph");
            params.param("line", "");

            endpointTargetFactory.forEndpoint(client, endpoint)
                    .path("disconnect_call")
                    .request()
                    .post(Entity.form(params), String.class);
        } catch (final RedirectionException e) {
            //this indicates a successful response from the endpoint
        } catch (final WebApplicationException e) {
            LOGGER.error("Endpoint returned status {}: {}", e.getResponse().getStatus(), e.getMessage());
        }
    }

    /*
     * Haven't found a way to set mic mute state from web interface, so using Telnet.
     */
    @Override
    public void setMicrophoneMute(final Endpoint endpoint, final boolean shouldMute) {
        try (TelnetConnection telnetConnection = telnetConnectionProvider.get()) {
            telnetConnection.setMagicWait(150); //how long to wait between commands

            final Endpoint telnetEndpoint = new Endpoint();
            telnetEndpoint.setIpAddress(endpoint.getIpAddress());
            telnetEndpoint.setPort(23); //use default telnet port, instead of any manually-specified http port
            telnetEndpoint.setPassword(endpoint.getPassword());

            final String welcomePrompt = telnetConnection.open(telnetEndpoint);

            if (welcomePrompt.contains("Password:") && telnetEndpoint.getPassword() != null) {
                telnetConnection.call(telnetEndpoint.getPassword());
            }

            final String command = (shouldMute) ? "mic off" : "mic on";
            telnetConnection.call(command);
        }
    }

}
