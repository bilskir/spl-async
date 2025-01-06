package bgu.spl.mics;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl mbInstance = new MessageBusImpl();;
	private final Map<Class<? extends Event<?>>,ConcurrentLinkedQueue<MicroService>> eventMap;
	private final Map<Class<? extends Broadcast>,ConcurrentLinkedQueue<MicroService>> broadcastMap;
	private final Map<MicroService,BlockingQueue<Message>> msQueues;
	private final Map<Event<?>,Future<?>> eventFutures;

	private CountDownLatch latch;

	public MessageBusImpl(){
		eventMap = new ConcurrentHashMap<Class<? extends Event<?>>,ConcurrentLinkedQueue<MicroService>>();
		broadcastMap = new ConcurrentHashMap<Class<? extends Broadcast>,ConcurrentLinkedQueue<MicroService>>();  
		msQueues = new ConcurrentHashMap<MicroService,BlockingQueue<Message>>();
		eventFutures = new ConcurrentHashMap<Event<?>,Future<?>>();
	}

	public static MessageBusImpl getInstance(){							// singleton - do not create 2 instances
		return mbInstance;		
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}
	    /**
     * Subscribes {@code m} to receive {@link Event}s of type {@code type}.
     * <p>
     * @param <T>  The type of the result expected by the completed event.
     * @param type The type to subscribe to,
     * @param m    The subscribing micro-service.
     */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {	// make sure to not override the already existed queue
		eventMap.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastMap.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
	}


	@Override
	public <T> void complete(Event<T> e, T result) {
		try {
			@SuppressWarnings("unchecked")
			Future<T> f= (Future<T>) eventFutures.get(e);
			f.resolve(result);
			eventFutures.remove(e); // The microservice has a pointer to the future so it wont be collected by the garbage collector
		} catch (Exception ex) {
			System.out.println("no such Event!");

		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		Queue<MicroService> subscribers = broadcastMap.get(b.getClass());
		if (subscribers != null) {
			synchronized(subscribers){

				for (MicroService m : subscribers) {
				    // System.out.println("Broadcast sent to: " + m.getName() + ", Broadcast: " + b.getClass().getSimpleName());

					BlockingQueue<Message> queue = msQueues.get(m);
						if (queue != null) {
							queue.add(b);
					}
				}
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
    Queue<MicroService> subscribers = eventMap.get(e.getClass());
    if (subscribers == null || subscribers.isEmpty()) {
        System.out.println("No subscribers for event: " + e.getClass().getSimpleName());
        return null;
    }

    MicroService m;
    synchronized (e.getClass()) {
        m = subscribers.poll();
        if (m != null) {
            subscribers.add(m);
        } else {
            System.out.println("No microservice available to handle event: " + e.getClass().getSimpleName());
            return null;
        }
    }

    Future<T> f = new Future<>();
    eventFutures.put(e, f);

    BlockingQueue<Message> queue = msQueues.get(m);
    if (queue == null) {
        System.out.println("MicroService queue is null for: " + m.getName());
        return null;
    }

    queue.add(e);
    // System.out.println("Event sent to: " + m.getName() + ", Event: " + e.getClass().getSimpleName());
    return f;
}


	@Override
	public void register(MicroService m) {
		System.out.println("MicroService: " + m.getName() + "got registered");
		msQueues.put(m, new LinkedBlockingQueue<Message>());
		if (latch != null) {
			latch.countDown();
		}
	}

	@Override
	public void unregister(MicroService m) {
		synchronized(eventMap){
			for (Queue<MicroService> queue : eventMap.values()) {
				queue.remove(m);
			}
		}
		synchronized(broadcastMap){
			for (Queue<MicroService> queue : broadcastMap.values()) {
				queue.remove(m);
			}
		}
		synchronized(msQueues){
			msQueues.remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> queue = msQueues.get(m);
		if (queue == null) {
			throw new IllegalStateException("Microservice not registered: " + m);
		}

		return queue.take();
	}
}
