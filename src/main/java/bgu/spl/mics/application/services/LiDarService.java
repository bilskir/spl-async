package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedCloudPoints;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.TrackedObject;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private int currentTick;
    private final int duration;
    private int crashTime;
    private final LiDarWorkerTracker lidarWorker;
    private final LiDarDataBase db = LiDarDataBase.getInstance("FilePath");
    private Object[] detectedObjects;


    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker, int duration) {
        super("LiDarService");
        this.lidarWorker = LiDarWorkerTracker;
        this.duration = duration;
        this.currentTick = 0;
        this.crashTime = -1;
        this.detectedObjects = new Object[duration + 1];
        for(int i = 0; i < detectedObjects.length; i++){
            detectedObjects[i] = new ArrayList<DetectObjectsEvent>();
        }
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeEvent(DetectObjectsEvent.class, msg -> {
            if(msg.getTimeSent() + lidarWorker.getFrequency() <= currentTick){
                handleDetectObjectEvent(msg, msg.getTimeSent() + lidarWorker.getFrequency());
            }

            else{
                // Add the event to the list of events to be processed at future ticks.
                int index = msg.getTimeSent() + lidarWorker.getFrequency();
                if(index <= duration){
                    ((ArrayList<DetectObjectsEvent>) detectedObjects[index]).add(msg);
                }
            }
        });

        subscribeBroadcast(TickBroadcast.class, msg -> {
            currentTick++;

             if(currentTick > duration){
                sendBroadcast(new TerminatedBroadcast(this.getName()));
                lidarWorker.setStatus(STATUS.DOWN);
                this.terminate();
            }

            else{
                if(detectedObjects[currentTick] != null){
                    // Process the events that were sent at the current tick.
                    for(DetectObjectsEvent event : (ArrayList<DetectObjectsEvent>) detectedObjects[currentTick]){
                        handleDetectObjectEvent(event, currentTick);
                    }
                }
               
            }
        });
   
        subscribeBroadcast(TerminatedBroadcast.class, msg -> {
            System.out.println(this.getName() +  " recived that " + msg.getSenderName() + " got terminated.");
        });

        subscribeBroadcast(CrashedBroadcast.class, msg -> {
           this.crashTime = msg.getCrashTime();
           terminate();
           this.lidarWorker.setStatus(STATUS.DOWN);
           System.out.println(this.getName() +  " recived that " + msg.getSenderName() + " Crashed Bandicoot.");

        });
    }


    private boolean handleDetectObjectEvent(DetectObjectsEvent event , int time){
        // Store relevant cloudPoints in a list and confirm if there is an error
        List<StampedCloudPoints> currentCloudPoints = new LinkedList<StampedCloudPoints>();
        for(StampedCloudPoints cp : db.getCloudPoints()){
            if(cp.getTime() == time){
                if(cp.getID() == "ERROR"){
                    lidarWorker.setStatus(STATUS.ERROR);
                    sendBroadcast(new CrashedBroadcast(getName(), time));
                    terminate();
                    complete(event, false);
                    return false;
                }

                currentCloudPoints.add(cp);
            }
        }

        // Create a list of TrackedObjects to send to FusionSLAM Based on the detected objects
        List<TrackedObject> trackedObjects = new LinkedList<TrackedObject>();
        for(StampedCloudPoints cp : currentCloudPoints){
            String desc;
            for(DetectedObject obj : event.getStampedDetectedObjects().getDetectedObjectsList()){
                if(obj.getID() == cp.getID()){
                    desc = obj.getDescription();
                    trackedObjects.add(new TrackedObject(cp.getID(), time, desc, cp.getCloudPoints()));
                }
            }
        }
        complete(event, true);
        sendEvent(new TrackedObjectsEvent(trackedObjects,time));
        return true;
    }

}
