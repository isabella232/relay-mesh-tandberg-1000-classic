package vc.bjn.catalyst.test.jersey.client;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import vc.bjn.catalyst.mesh.tandberg1000classic.connection.EndpointTargetFactory;
import vc.bjn.catalyst.mesh.tandberg1000classic.connection.TestEndpointTargetFactory;

import javax.inject.Provider;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * All-in-one mocking of Jersey 2 client requests for classes under test which inject a Provider{@code <Client>}.
 * <p>
 * <h1>Usage</h1>
 * <p>
 * <h2><code>MyService.java</code></h2>
 * <pre>
 * {@code @}Service
 * public class MyService {
 *
 *     {@code @}Autowired private Provider{@code <Client>} httpClientProvider;
 *
 *     public String fetch(){
 *         try (AutoClosableClient client = new AutoClosableClientImpl(httpClientProvider.get())) {
 *
 *             String response = client.target("http://127.0.0.1")
 *                 .path("path")
 *                 .queryParam("query", "value")
 *                 .request()
 *                 .get(String.class);
 *             return response;
 *
 *         } catch(WebApplicationException | ProcessingException e){
 *             throw new RuntimeException("HTTP error", e);
 *         }
 *     }
 * }</pre>
 * <p>
 * <h2><code>MyServiceTest.java</code></h2>
 * <pre>
 * public class MyServiceTest {
 *
 *     private MyService myService;
 *     private TestClientProvider clientProvider;
 *
 *     {@code @}BeforeMethod
 *     private void init(){
 *         myService = new MyService();
 *         clientProvider = new TestClientProvider();
 *         Whitebox.setInternalState(myService, clientProvider);
 *     }
 *
 *     {@code @}Test
 *     public void testMyServiceFetch(){
 *         TestBuilder mockRequest = mock(TestBuilder.class);
 *         when(mockRequest.get(String.class)).thenReturn("sample text/plain HTTP response body");
 *         mockClientProvider.enqueueMockBuilder(mockRequest);
 *
 *         String actual = myService.fetch();
 *         assertEquals(actual, "sample text/plain HTTP response body");
 *
 *         verify(mockRequest).get(eq(String.class));
 *         verify(mockRequest).requested("http://127.0.0.1/path?query=value");
 *     }
 * }</pre>
 *
 * @see TestEndpointTargetFactory in case your service uses a {@link EndpointTargetFactory} instead of a {@code Provider<Client>}.
 */
public class TestClientProvider implements Provider<Client>, TestResponseProvider {

    private final TestResponseProvider testResponseProvider;

    public TestClientProvider(final TestResponseProvider testResponseProvider) {
        this.testResponseProvider = testResponseProvider;
    }

    public TestClientProvider() {
        this(new TestResponseProviderImpl());
    }

    @Override
    public Client get() {
        final Client mockClient = mock(Client.class);

        when(mockClient.target(anyString())).thenAnswer(new Answer<WebTarget>() {
            @Override
            public WebTarget answer(final InvocationOnMock invocation) {
                final String uri = invocation.getArgumentAt(0, String.class);
                return new TestJerseyWebTarget(uri, testResponseProvider);
            }
        });

        when(mockClient.target(any(URI.class))).thenAnswer(new Answer<WebTarget>() {
            @Override
            public WebTarget answer(final InvocationOnMock invocation) {
                final URI uri = invocation.getArgumentAt(0, URI.class);
                return new TestJerseyWebTarget(uri, testResponseProvider);
            }
        });

        when(mockClient.target(any(UriBuilder.class))).thenAnswer(new Answer<WebTarget>() {
            @Override
            public WebTarget answer(final InvocationOnMock invocation) {
                final UriBuilder uriBuilder = invocation.getArgumentAt(0, UriBuilder.class);
                final URI uri = uriBuilder.build();
                return new TestJerseyWebTarget(uri, testResponseProvider);
            }
        });

        return mockClient;
    }

    @Override
    public void enqueueMockBuilder(final TestBuilder mockBuilder) {
        testResponseProvider.enqueueMockBuilder(mockBuilder);
    }

    @Override
    public void reset() {
        testResponseProvider.reset();
    }

    @Override
    public TestBuilder getNextMockBuilder() {
        return testResponseProvider.getNextMockBuilder();
    }

    @Override
    public TestBuilder addNewMockBuilder() {
        return testResponseProvider.addNewMockBuilder();
    }

}
