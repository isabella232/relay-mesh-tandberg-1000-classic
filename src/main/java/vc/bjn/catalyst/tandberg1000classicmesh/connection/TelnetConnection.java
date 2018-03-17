package vc.bjn.catalyst.tandberg1000classicmesh.connection;

import vc.bjn.catalyst.tandberg1000classicmesh.data.Endpoint;

public interface TelnetConnection extends AutoCloseable {
    String open(Endpoint endpoint);

    String call(String request);

    boolean isConnected();

    int getMagicWait();

    void setMagicWait(int magicWait);

    @Override
    void close();
}
