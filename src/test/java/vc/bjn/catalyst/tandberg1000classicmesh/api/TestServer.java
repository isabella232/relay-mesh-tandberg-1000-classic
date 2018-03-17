package vc.bjn.catalyst.tandberg1000classicmesh.api;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer;
import org.mockito.internal.util.MockUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import vc.bjn.catalyst.tandberg1000classicmesh.ServerStarter;
import vc.bjn.catalyst.tandberg1000classicmesh.TestBinder;

import javax.ws.rs.ProcessingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

public class TestServer implements ISuiteListener, ITestListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestServer.class);

    private static final Random RANDOM = new Random();
    private static final MockUtil MOCK_UTIL = new MockUtil();
    private static final Filter MOCK_FILTER = new MockFilter();
    private static final TestBinder BINDER = new TestBinder();

    private static HttpServer testServer;
    private static URI baseUri;

    public static URI getBaseUri() {
        return baseUri;
    }

    public static <T> T getService(final Class<T> contractOrImpl) {
        return getServiceLocator().getService(contractOrImpl);
    }

    private static ServiceLocator getServiceLocator() {
        final GrizzlyHttpContainer container = (GrizzlyHttpContainer) testServer.getServerConfiguration().getHttpHandlersWithMapping().keySet().iterator().next();
        return container.getApplicationHandler().getServiceLocator();
    }

    @Override
    public void onStart(final ISuite suite) {
        while (testServer == null) {
            try {
                final int port = RANDOM.nextInt(65536 - 1024) + 1024 + 1;
                baseUri = new URI("http", null, "0.0.0.0", port, null, null, null);
                testServer = ServerStarter.startServer(baseUri, BINDER);
            } catch (URISyntaxException | ProcessingException e) {
                //try again with a different random port
            }
        }
    }

    @Override
    public void onFinish(final ISuite suite) {
        if (testServer != null) {
            testServer.shutdown();
        }
        testServer = null;
    }

    private static void resetMocks() {
        final List<?> mockServices = getServiceLocator().getAllServices(MOCK_FILTER);
        for (final Object mockService : mockServices) {
            if (MOCK_UTIL.isMock(mockService)) {
                MOCK_UTIL.resetMock(mockService);
            }
        }
    }

    @Override
    public void onTestSuccess(final ITestResult result) {
        resetMocks();
    }

    @Override
    public void onTestFailure(final ITestResult result) {
        resetMocks();
    }

    @Override
    public void onTestSkipped(final ITestResult result) {
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(final ITestResult result) {
    }

    @Override
    public void onStart(final ITestContext context) {
    }

    @Override
    public void onFinish(final ITestContext context) {
    }


    @Override
    public void onTestStart(final ITestResult result) {
    }

    private static final class MockFilter implements Filter {
        @Override
        public boolean matches(final Descriptor descriptor) {
            final List<String> isMock = descriptor.getMetadata().get(TestBinder.MOCK_METADATA_KEY);
            return isMock != null && Boolean.valueOf(isMock.get(0));
        }
    }
}
