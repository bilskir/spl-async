

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBusImpl messageBus;
    private MicroService microService1;
    private MicroService microService2;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        microService1 = new MicroService("MicroService1") {
            @Override
            protected void initialize() {
            }
        
        };
        microService2 = new MicroService("MicroService2") {
            @Override
            protected void initialize() {
            }
        };
    }

    @Test
    void testGetInstance() {
        assertNotNull(messageBus, "MessageBusImpl instance should not be null");
        assertSame(messageBus, MessageBusImpl.getInstance(), "Should return the same instance (singleton)");
    }

    @Test
    void testSendBroadcast() {
        class TestBroadcast implements Broadcast {}
        messageBus.register(microService1);
        messageBus.register(microService2);
        messageBus.subscribeBroadcast(TestBroadcast.class, microService1);
        messageBus.subscribeBroadcast(TestBroadcast.class, microService2);

        Broadcast broadcast = new TestBroadcast();
        messageBus.sendBroadcast(broadcast);

        assertDoesNotThrow(() -> {
            assertEquals(broadcast, messageBus.awaitMessage(microService1));
            assertEquals(broadcast, messageBus.awaitMessage(microService2));
        });
    }

    @Test
    void testComplete() {
        class TestEvent implements Event<Boolean> {}
        messageBus.register(microService1);
        messageBus.subscribeEvent(TestEvent.class, microService1);

        Event<Boolean> event = new TestEvent();
        Future<Boolean> future = messageBus.sendEvent(event);

        messageBus.complete(event, true);

        assertTrue(future.isDone(), "Future should be completed after calling complete()");
        assertEquals(true, future.get(), "Future result should match the completion value");
    }
}
