package vc.bjn.catalyst.mesh.tandberg1000classic;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import vc.bjn.catalyst.mesh.tandberg1000classic.dispatcher.Dispatcher;

import javax.inject.Singleton;

import static org.mockito.Mockito.mock;

/**
 * Like {@link MainBinder} but for tests.
 */
public class TestBinder extends AbstractBinder {

    public static final String MOCK_METADATA_KEY = "isMock";

    @Override
    protected void configure() {
        bindMock(Dispatcher.class);
    }

    private void bindMock(final Class<?> mockClass) {
        bindFactory(new MockFactory<>(mockClass)).to(mockClass)
                .withMetadata("isMock", Boolean.TRUE.toString())
                .in(Singleton.class);
    }

    private static class MockFactory<T> implements Factory<T> {
        private final Class<T> mockClass;

        MockFactory(final Class<T> mockClass) {
            this.mockClass = mockClass;
        }

        @Override
        public T provide() {
            return mock(mockClass);
        }

        @Override
        public void dispose(final T instance) {
        }
    }

}

