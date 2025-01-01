package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private GPSIMU gpsimu;
    private int currentTick;
    private final int duration; 
    private int crashTime;

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu, int duration) {
        super("Pose Service");
        this.gpsimu = gpsimu;
        this.currentTick = 0;
        this.duration = duration;
        crashTime = -1;

    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, msg -> {
            currentTick++;

            if(currentTick > duration){
                gpsimu.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(this.getName()));
                this.terminate();
            }

            else{
                sendEvent(new PoseEvent(gpsimu.getPoseList().get(currentTick)));
            }
        });
        
        subscribeBroadcast(TerminatedBroadcast.class, msg -> {
            System.out.println(this.getName() +  " recived that " + msg.getSenderName() + " got terminated.");
            // Add Log
        });

         subscribeBroadcast(CrashedBroadcast.class, msg -> {
           this.crashTime = msg.getCrashTime();
           this.terminate();
           this.gpsimu.setStatus(STATUS.DOWN);
           System.out.println(this.getName() +  " recived that " + msg.getSenderName() + " Crashed Bandicoot.");
        });        
    }
}
