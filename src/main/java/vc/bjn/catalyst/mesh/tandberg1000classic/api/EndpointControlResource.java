package vc.bjn.catalyst.mesh.tandberg1000classic.api;

import org.glassfish.jersey.internal.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vc.bjn.catalyst.mesh.tandberg1000classic.data.Endpoint;
import vc.bjn.catalyst.mesh.tandberg1000classic.data.EndpointStatus;
import vc.bjn.catalyst.mesh.tandberg1000classic.data.Meeting;
import vc.bjn.catalyst.mesh.tandberg1000classic.dispatcher.Dispatcher;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EndpointControlResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointControlResource.class);

    @Inject private Dispatcher dispatcher;
    @Context private HttpHeaders requestHeaders;

    /**
     * @see <a href="https://relay.bluejeans.com/docs/mesh.html#capabilities">https://relay.bluejeans.com/docs/mesh.html#capabilities</a>
     */
    @GET
    @Path("{ipAddress}/capabilities")
    public Map<String, Boolean> capabilities(@PathParam("ipAddress") final String ipAddress,
                                             @QueryParam("port") final Integer port,
                                             @QueryParam("name") final String name) {
        LOGGER.info("Received capabilities request: ipAddress = {}, port = {}, name = {}", ipAddress, port, name);

        final Map<String, Boolean> capabilities = new HashMap<>();
        capabilities.put("JOIN", true);
        capabilities.put("HANGUP", true);
        capabilities.put("STATUS", true);
        capabilities.put("MUTE_MICROPHONE", true);
        capabilities.put("CALENDAR_PUSH", false);
        return capabilities;
    }

    /**
     * @see <a href="https://relay.bluejeans.com/docs/mesh.html#status">https://relay.bluejeans.com/docs/mesh.html#status</a>
     */
    @GET
    @Path("{ipAddress}/status")
    public Map<String, Boolean> status(@PathParam("ipAddress") final String ipAddress,
                                       @QueryParam("port") final Integer port,
                                       @QueryParam("name") final String name) {
        LOGGER.info("Received status request: ipAddress = {}, port = {}, name = {}", ipAddress, port, name);

        final Endpoint endpoint = new Endpoint();
        endpoint.setIpAddress(ipAddress);
        endpoint.setPort(port);
        endpoint.setName(name);
        setEndpointCredentials(endpoint);

        final EndpointStatus status = dispatcher.getStatus(endpoint);

        final Map<String, Boolean> response = new HashMap<>();
        response.put("callActive", status.isCallActive);
        response.put("microphoneMuted", status.isMicrophoneMuted);
        return response;
    }

    /**
     * @see <a href="https://relay.bluejeans.com/docs/mesh.html#join">https://relay.bluejeans.com/docs/mesh.html#join</a>
     */
    @POST
    @Path("{ipAddress}/join")
    public Response join(@PathParam("ipAddress") final String ipAddress,
                         @QueryParam("dialString") final String dialString,
                         @QueryParam("meetingId") final String meetingId,
                         @QueryParam("passcode") @DefaultValue("") String passcode,
                         @QueryParam("bridgeAddress") final String bridgeAddress,
                         final Endpoint endpoint) {
        if (passcode != null && passcode.isEmpty()) {
            passcode = null;
        }

        LOGGER.info("Received join request: ipAddress = {}, dialString = {}, meetingId = {}, passcode = {}, bridgeAddress = {}, endpoint = {}",
                ipAddress, dialString, meetingId, passcode, bridgeAddress, endpoint);

        setEndpointCredentials(endpoint);

        final Meeting meeting = new Meeting();
        meeting.setBridgeAddress(bridgeAddress);
        meeting.setDialString(dialString);
        meeting.setMeetingId(meetingId);
        meeting.setPasscode(passcode);

        dispatcher.join(endpoint, meeting);

        return Response.noContent().build();
    }

    /**
     * @see <a href="https://relay.bluejeans.com/docs/mesh.html#hangup">https://relay.bluejeans.com/docs/mesh.html#hangup</a>
     */
    @POST
    @Path("{ipAddress}/hangup")
    public Response hangup(@PathParam("ipAddress") final String ipAddress, final Endpoint endpoint) {
        LOGGER.info("Received hangUp request: ipAddress = {}, endpoint = {}", ipAddress, endpoint);

        setEndpointCredentials(endpoint);

        dispatcher.hangUp(endpoint);

        return Response.noContent().build();
    }

    /**
     * @see <a href="https://relay.bluejeans.com/docs/mesh.html#mutemicrophone">https://relay.bluejeans.com/docs/mesh.html#mutemicrophone</a>
     */
    @POST
    @Path("{ipAddress}/mutemicrophone")
    public void muteMicrophone(@PathParam("ipAddress") final String ipAddress, final Endpoint endpoint) {
        LOGGER.info("Received microphone mute request: ipAddress = {}, endpoint = {}", ipAddress, endpoint);

        setEndpointCredentials(endpoint);

        dispatcher.setMicrophoneMute(endpoint, true);
    }

    /**
     * @see <a href="https://relay.bluejeans.com/docs/mesh.html#mutemicrophone">https://relay.bluejeans.com/docs/mesh.html#mutemicrophone</a>
     */
    @POST
    @Path("{ipAddress}/unmutemicrophone")
    public void unmuteMicrophone(@PathParam("ipAddress") final String ipAddress, final Endpoint endpoint) {
        LOGGER.info("Received microphone unmute request: ipAddress = {}, endpoint = {}", ipAddress, endpoint);

        setEndpointCredentials(endpoint);

        dispatcher.setMicrophoneMute(endpoint, false);
    }

    private void setEndpointCredentials(final Endpoint endpoint) {
        final String authHeader = requestHeaders.getHeaderString(HttpHeaders.AUTHORIZATION);
        final String prefix = "Basic ";
        if (authHeader != null && authHeader.startsWith(prefix)) {
            final String decoded = Base64.decodeAsString(authHeader.substring(prefix.length()));
            final String[] split = decoded.split(":", 2); //assume no colons in username

            if (split.length == 2) {
                endpoint.setUsername(split[0]);
                endpoint.setPassword(split[1]);
            }
        }
    }

}
