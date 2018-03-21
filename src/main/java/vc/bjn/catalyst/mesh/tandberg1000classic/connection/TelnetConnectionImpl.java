package vc.bjn.catalyst.mesh.tandberg1000classic.connection;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vc.bjn.catalyst.mesh.tandberg1000classic.data.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TelnetConnectionImpl implements TelnetConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelnetConnectionImpl.class);

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final int DEFAULT_MAGIC_WAIT = 700; //ugh

    private final TelnetClient client;
    private PrintStream toEndpoint;
    private InputStream fromEndpoint;
    private final byte[] readBuffer = new byte[1024];
    private int magicWait = DEFAULT_MAGIC_WAIT;

    public TelnetConnectionImpl() {
        client = new TelnetClient();
        client.setConnectTimeout(15000);
        client.setDefaultTimeout(15000);
        client.setReaderThread(false); //prevent deadlock in BufferedReader.isAvailable
    }

    @Override
    public String open(final Endpoint endpoint) {
        Integer port = endpoint.getPort();
        if (port == null) {
            port = 23;
        }

        try {
            LOGGER.debug("Connecting to {}:{}...", endpoint.getIpAddress(), port);
            client.connect(endpoint.getIpAddress(), port);
            LOGGER.debug("Connected.");

            toEndpoint = new PrintStream(client.getOutputStream());
            fromEndpoint = client.getInputStream();
            readBuffer[0] = (byte) fromEndpoint.read(); //required because Telnet reader thread is disabled

            pause();

            return readAll(1);
        } catch (final InterruptedIOException e) {
            throw new RuntimeException("Timed out: " + e.getMessage(), e);
        } catch (final IOException e) {
            throw new RuntimeException("Unreachable: " + e.getMessage(), e);
        }
    }

    private String readAll(final int bufferWriteStartPosition) throws IOException {
        final int bytesRead = fromEndpoint.read(readBuffer, bufferWriteStartPosition, readBuffer.length - bufferWriteStartPosition);
        final String read = new String(readBuffer, 0, bytesRead + bufferWriteStartPosition, CHARSET);
        LOGGER.debug("<< {}", read);
        return read;
    }

    @Override
    public String call(final String request) {
        write(request);
        pause();
        try {
            return readAll(0);
        } catch (final IOException e) {
            throw new RuntimeException("Telnet command failed: " + e.getMessage(), e);
        }
    }

    private void pause() {
        try {
            Thread.sleep(magicWait);
        } catch (final InterruptedException e) {
            //continue
        }
    }

    @Override
    public void close() {
        try {
            client.disconnect();
        } catch (final IOException e) {
            // Oh well
        }
    }

    @Override
    public boolean isConnected() {
        return client.isConnected();
    }

    private void write(final String toWrite) {
        LOGGER.debug(">> {}", toWrite);
        toEndpoint.println(toWrite);
        toEndpoint.flush();
    }

    @Override
    public int getMagicWait() {
        return magicWait;
    }

    @Override
    public void setMagicWait(final int magicWait) {
        this.magicWait = magicWait;
    }
}
