package vc.bjn.catalyst.mesh.tandberg1000classic.connection;

import vc.bjn.catalyst.mesh.tandberg1000classic.data.Endpoint;

public interface TelnetConnection extends AutoCloseable {
    String open(Endpoint endpoint);

    String call(String request);

    boolean isConnected();

    int getMagicWait();

    void setMagicWait(int magicWait);

    @Override
    void close();
}
