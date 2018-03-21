package vc.bjn.catalyst.mesh.tandberg1000classic.dispatcher;

import vc.bjn.catalyst.mesh.tandberg1000classic.data.Endpoint;
import vc.bjn.catalyst.mesh.tandberg1000classic.data.EndpointStatus;
import vc.bjn.catalyst.mesh.tandberg1000classic.data.Meeting;

public interface Dispatcher {
    EndpointStatus getStatus(Endpoint endpoint);

    void join(Endpoint endpoint, Meeting meeting);

    void hangUp(Endpoint endpoint);

    void setMicrophoneMute(Endpoint endpoint, boolean shouldMute);
}
