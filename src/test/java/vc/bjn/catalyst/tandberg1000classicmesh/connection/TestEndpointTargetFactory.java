package vc.bjn.catalyst.tandberg1000classicmesh.connection;

import vc.bjn.catalyst.tandberg1000classicmesh.data.Endpoint;
import vc.bjn.catalyst.tandberg1000classicmesh.dispatcher.Dispatcher;
import vc.bjn.catalyst.test.jersey.client.TestBuilder;
import vc.bjn.catalyst.test.jersey.client.TestJerseyWebTarget;
import vc.bjn.catalyst.test.jersey.client.TestResponseProvider;
import vc.bjn.catalyst.test.jersey.client.TestResponseProviderImpl;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.net.URI;

/**
 * Used in {@code @BeforeMocks} to give a {@link Dispatcher} a {@link TestResponseProvider} which can return mock responses.
 * <p>
 * <h1>Usage</h1>
 * <p>
 * <h2><code>MyDispatcher.java</code></h2>
 * <pre>
 * {@code @}Component
 * {@code @}Profile(Profiles.DEFAULT)
 * public class MyDispatcher implements Dispatcher {
 *
 *     {@code @}Autowired private EndpointTargetFactory endpointTargetFactory;
 *
 *     {@code @}Override
 *     public void dial(final Endpoint endpoint, final String bridgeAddress) throws ExternalSystemException {
 *         try (AutoClosableClient client = endpointTargetFactory.newAutoClosableClient()) {
 *             final Map{@code <String, Object>} params = new HashMap{@code <>}();
 *             params.put("number", bridgeAddress);
 *
 *             endpointTargetFactory.getTarget(client, endpoint)
 *                     .path("api/calls/dial")
 *                     .request()
 *                     .post(Entity.json(params), String.class);
 *         } catch (ProcessingException | WebApplicationException e) {
 *             throw transformHttpException(e, endpoint);
 *         }
 *     }
 * }</pre>
 * <p>
 * <h2><code>MyDispatcherTest.java</code></h2>
 * <pre>
 * public class MyDispatcherTest {
 *
 *     {@code @}Captor private ArgumentCaptor{@code <Entity<Map<String, Object>>>} entityCaptor;
 *
 *     private MyDispatcher dispatcher;
 *     private TestEndpointTargetFactory endpointTargetFactory;
 *     private Endpoint endpoint;
 *
 *     {@code @}BeforeMethod
 *     protected void init() {
 *         MockitoAnnotations.initMocks(this);
 *         dispatcher = new MyDispatcher();
 *         endpointTargetFactory = new TestEndpointTargetFactory();
 *         Whitebox.setInternalState(dispatcher, endpointTargetFactory);
 *
 *         endpoint = new Endpoint();
 *         endpoint.setIpAddress(InetAddresses.forString("1.2.3.4"));
 *         endpoint.setControlProtocol(ControlProtocol.MY_CONTROL_PROTOCOL);
 *     }
 *
 *     {@code @}Test
 *     public void dial() throws ExternalSystemException {
 *         final TestBuilder mockDialRequest = endpointTargetFactory.addNewMockBuilder();
 *         when(mockDialRequest.post(any(Entity.class), eq(String.class))).thenReturn("success");
 *
 *         dispatcher.dial(endpoint, "bjn.vc");
 *
 *         verify(mockDialRequest).requested("https://1.2.3.4:443/api/calls/dial");
 *         verify(mockDialRequest).post(entityCaptor.capture(), eq(String.class));
 *         final Entity{@code <Map<String, Object>>} postEntity = entityCaptor.getValue();
 *         final Map{@code <String, Object>} params = postEntity.getEntity();
 *         assertEquals(params.get("number"), "bjn.vc");
 *     }
 * }</pre>
 */
public class TestEndpointTargetFactory extends EndpointTargetFactoryImpl implements TestResponseProvider {

    @Override
    public WebTarget configureAuth(final Endpoint endpoint, final WebTarget target) {
        return target;
    }

    private final TestResponseProvider testResponseProvider = new TestResponseProviderImpl();

    @Override
    public WebTarget getTarget(final Client client, final URI uri) {
        return new TestJerseyWebTarget(uri, this);
    }

    @Override
    public void enqueueMockBuilder(final TestBuilder mockBuilder) {
        testResponseProvider.enqueueMockBuilder(mockBuilder);
    }

    @Override
    public TestBuilder getNextMockBuilder() {
        return testResponseProvider.getNextMockBuilder();
    }

    @Override
    public void reset() {
        testResponseProvider.reset();
    }

    @Override
    public TestBuilder addNewMockBuilder() {
        return testResponseProvider.addNewMockBuilder();
    }

}
