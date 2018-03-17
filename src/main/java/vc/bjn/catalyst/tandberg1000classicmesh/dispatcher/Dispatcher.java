package vc.bjn.catalyst.tandberg1000classicmesh.dispatcher;

import vc.bjn.catalyst.tandberg1000classicmesh.data.Endpoint;
import vc.bjn.catalyst.tandberg1000classicmesh.data.EndpointStatus;
import vc.bjn.catalyst.tandberg1000classicmesh.data.Meeting;

public interface Dispatcher {
    EndpointStatus getStatus(Endpoint endpoint);

    void join(Endpoint endpoint, Meeting meeting);

    void hangUp(Endpoint endpoint);

    void setMicrophoneMute(Endpoint endpoint, boolean shouldMute);
}
