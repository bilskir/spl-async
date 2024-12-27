package bgu.spl.mics;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl mbInstance = null;
	private final Dictionary<Class<? extends Event<?>>,BlockingQueue<MicroService>> eventMap;
	private final Dictionary<Class<? extends Broadcast>,BlockingQueue<MicroService>> broadcastMap;
	private final Dictionary<MicroService,BlockingQueue<Message>> msQueues;
	private final Dictionary<Event<?>,Future<?>> eventFutures;



	public MessageBusImpl(){
		eventMap = new Hashtable<Class<? extends Event<?>>,BlockingQueue<MicroService>>();
		broadcastMap = new Hashtable<Class<? extends Broadcast>,BlockingQueue<MicroService>>();  
		msQueues = new Hashtable<MicroService,BlockingQueue<Message>>();
		eventFutures = new Hashtable<Event<?>,Future<?>>();
	}

	public static MessageBusImpl getInstance(){
		if(mbInstance==null){
			mbInstance = new MessageBusImpl();
		}
		return mbInstance;		
	}

	    /**
     * Subscribes {@code m} to receive {@link Event}s of type {@code type}.
     * <p>
     * @param <T>  The type of the result expected by the completed event.
     * @param type The type to subscribe to,
     * @param m    The subscribing micro-service.
     */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		try {
			eventMap.get(type).add(m);
		} catch (NullPointerException e) {
			eventMap.put(type, new LinkedBlockingQueue<MicroService>());
			eventMap.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		try {
			broadcastMap.get(type).add(m);
		} catch (NullPointerException e) {
			broadcastMap.put(type, new LinkedBlockingQueue<MicroService>());
			broadcastMap.get(type).add(m);
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		try {
			@SuppressWarnings("unchecked")
			Future<T> f= (Future<T>) eventFutures.get(e);
			f.resolve(result);
			
		} catch (Exception ex) {
			System.out.println("no such Event!");
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for(MicroService m : broadcastMap.get(b)){
			if(msQueues.get(m)==null){
				broadcastMap.get(b).remove(m);
			}
			else{
				msQueues.get(m).add(b);
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		MicroService m = eventMap.get(e.getClass()).remove();
		while(msQueues.get(m)== null){
			m = eventMap.get(e.getClass()).remove();
		}
		eventMap.get(e.getClass()).add(m);
		
		Future<T> f = new Future<T>();
		eventFutures.put(e, f);
		msQueues.get(m).add(e);
		msQueues.get(m).notify();
		return f;
	}

	@Override
	public void register(MicroService m) {
		msQueues.put(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		msQueues.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
			return msQueues.get(m).remove();
	}
}
