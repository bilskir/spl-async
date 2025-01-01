package bgu.spl.mics.application.services;

import java.util.LinkedList;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.TrackedObject;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */


    private final FusionSlam fusionSlam;
    private final int duration;
    private Object[] trackedObjects;
    private Pose[] poses;
    private int currentTick;
    private int crashTime;



    public FusionSlamService(FusionSlam fusionSlam, int duration) {
        super("FusionSlamService");
        this.currentTick = 0;
        this.fusionSlam = fusionSlam;
        this.duration = duration;
        this.crashTime = -1;
        this.poses = new Pose[duration + 1];
        this.trackedObjects = new Object[duration + 1];
        for (int i = 0; i < duration + 1; i++) {
            this.trackedObjects[i] = new LinkedList<TrackedObjectsEvent>();
        }

    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {


        subscribeBroadcast(TickBroadcast.class, msg -> {
            currentTick++;
 
            // If the current tick is greater than the duration, terminate the service.
            if(currentTick > duration){
                sendBroadcast(new TerminatedBroadcast(this.getName()));
                this.terminate();
            }

            else{
                
            }
        });














        subscribeBroadcast(CrashedBroadcast.class, msg -> {
            crashTime = msg.getCrashTime();
            terminate();
            System.out.println(this.getName() +  " recived that " + msg.getSenderName() + " Crashed Bandicoot.");
            // TODO: Send log until crash time
        });


        subscribeBroadcast(TerminatedBroadcast.class, msg -> {
            System.out.println(this.getName() +  " recived that " + msg.getSenderName() + " got terminated.");
        });


    }
}
