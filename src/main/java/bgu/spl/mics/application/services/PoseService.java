package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
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
    private int crashTime;

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("Pose Service");
        this.gpsimu = gpsimu;
        crashTime = -1;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, msg -> {
            int currentTick = msg.getTick();
            gpsimu.setCurrentTick(currentTick);

            for (Pose pose : gpsimu.getPoseList()) {
                if (pose.getTime() == currentTick) {
                    sendEvent(new PoseEvent(pose));
                    gpsimu.getPoseList().remove(pose);
                    break;
                }
            }
            if(gpsimu.getPoseList().isEmpty()){
                this.gpsimu.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                this.terminate();
            }
        });
        
        subscribeBroadcast(TerminatedBroadcast.class, msg -> {
            System.out.println(this.getName() +  " recived that " + msg.getSenderName() + " got terminated.");
            if (msg.getSenderName() == "TimeService") {
                this.gpsimu.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                this.terminate();
                // log
            }
        });

         subscribeBroadcast(CrashedBroadcast.class, msg -> {
           System.out.println(this.getName() +  " recived that " + msg.getSenderName() + " got crashed.");
           this.crashTime = msg.getCrashTime();
           this.gpsimu.setStatus(STATUS.DOWN);
           this.terminate();
        });
        
    }

    @Override
    public String toString(){
        return gpsimu.toString();
    }
}
