package bgu.spl.mics.application.services;

import java.util.LinkedList;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
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
    private int sensorsCounter;
    private int crashTime; 
  


    public FusionSlamService(int sensorsCounter) {
        super("FusionSlamService");
        fusionSlam = FusionSlam.getInstance();
        this.sensorsCounter = sensorsCounter;
        this.crashTime = -1;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeEvent(PoseEvent.class, msg -> {
            Pose pose = msg.getCurrentPose();
            fusionSlam.addPose(pose);
        });

        subscribeEvent(TrackedObjectsEvent.class, msg -> {
            LinkedList<TrackedObject> trackedObjects = msg.getTrackedObjects();
            for(TrackedObject trackedObject : trackedObjects){
                fusionSlam.addTrackedObject(trackedObject);
            }
        });


        subscribeBroadcast(TickBroadcast.class, msg -> {
            int currentTick = msg.getTick();
            fusionSlam.calculateMap(currentTick);
        });

        subscribeBroadcast(TerminatedBroadcast.class, msg -> {
            System.out.println(this.getName() + "recieved that " + msg.getSenderName() +  " terminated");
            if(msg.getSenderName() == "TimeService"){
                sendBroadcast(new TerminatedBroadcast(this.getName()));
                terminate();
                //send log
            }

            else if(msg.getSenderName().contains("LiDarService") || msg.getSenderName().contains("CameraService") || msg.getSenderName().contains("PoseService")){
                sensorsCounter--;
                if(sensorsCounter == 0){
                    sendBroadcast(new TerminatedBroadcast(this.getName()));
                    terminate();
                    //send log
                }
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, msg -> {
            System.out.println(this.getName() + "recieved that " + msg.getSenderName() +  " crashed");
            this.crashTime = msg.getCrashTime();
            sendBroadcast(new TerminatedBroadcast(this.getName()));
            terminate();
            //send log
        });

    }
    
    public FusionSlam getFusionSlam() {
        return fusionSlam;
    }

    @Override
    public String toString() { return "FusionSlamService{" + "fusionSlam=" + fusionSlam + ", sensorsCounter=" + sensorsCounter + ", crashTime=" + crashTime + ", serviceName='" + getName() + '\'' + '}'; }
}
