package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.StartSimulationEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private final long tickTime;
    private int duration;
    private int currentTick;
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
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {

        this.subscribeEvent(StartSimulationEvent.class, (msg)->{
            while(currentTick < duration){
                currentTick++;
                StatisticalFolder.getInstance().addSystemRuntime(1);
                sendBroadcast(new TickBroadcast(duration,currentTick));
                try {
                    wait(tickTime);
                } catch (Exception e) {
                    
                }
            }
    
            sendBroadcast(new TerminatedBroadcast(this.getName()));
            this.terminate();
        });
        
        this.subscribeBroadcast(CrashedBroadcast.class, (msg)->{
            System.out.println(this.getName() + "recieved that " + msg.getSenderName() +  " crashed");
            //log
            sendBroadcast(new TerminatedBroadcast(this.getName()));
            this.terminate();
        });
        




       
    }
}
