package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.StartSimulationEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private final long tickTime;
    private final int duration;
    private int currentTick;
    private volatile boolean running;
    private final ExecutorService executor;

    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.tickTime = TickTime;
        this.duration = Duration;
        this.currentTick = 0;
        this.running = true;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        // Start the simulation in a separate thread
        this.subscribeEvent(StartSimulationEvent.class, (msg) -> {
            System.out.println("Simulation Started!");
            executor.submit(() -> runTickLoop());
        });

        // Listen for CrashedBroadcast
        this.subscribeBroadcast(CrashedBroadcast.class, (msg) -> {
            System.out.println(this.getName() + " received that " + msg.getSenderName() + " crashed");
            stopTickLoop();
        });

        // Listen for TerminatedBroadcast
        this.subscribeBroadcast(TerminatedBroadcast.class, (msg) -> {
            System.out.println(this.getName() + " received that " + msg.getSenderName() + " terminated");
            if (msg.getSenderName().contains("FusionSlamService") || msg.getSenderName().contains(getName())) {
                stopTickLoop();
            }
        });
    }

    /**
     * Runs the tick loop in a separate thread.
     */
    private void runTickLoop() {
        while (currentTick < duration && running) {
            System.out.println("Current tick: " + currentTick);
            currentTick ++;
            StatisticalFolder.getInstance().addSystemRuntime(1);
            sendBroadcast(new TickBroadcast(duration, currentTick));

            try {
                Thread.sleep(tickTime);
            } catch (InterruptedException e) {
                System.out.println("TimeService interrupted!");
                break;
            }
        }


        // Send termination signal when the tick loop ends
        sendBroadcast(new TerminatedBroadcast(this.getName()));
        this.terminate();
    }

    /**
     * Stops the tick loop.
     */
    private void stopTickLoop() {
        running = false;
        executor.shutdownNow();
    }
}
