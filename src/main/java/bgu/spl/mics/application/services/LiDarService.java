package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedCloudPoints;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
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
    private final LiDarWorkerTracker lidarWorker;
    private int crashTime;


    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LiDarService" + LiDarWorkerTracker.getID());
        this.lidarWorker = LiDarWorkerTracker;        
        this.crashTime = -1;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeEvent(DetectObjectsEvent.class, msg -> {   
            if(!lidarWorker.isError(msg.getTimeSent())){
                List<DetectedObject> detectedObjects = msg.getStampedDetectedObjects().getDetectedObjectsList();
                for(DetectedObject object : detectedObjects){
                    List<CloudPoint> coordinates = lidarWorker.getCoordinates(object, msg.getTimeSent());
                    lidarWorker.getDataBase().removeCloudPoint(object, msg.getTimeSent());
                    lidarWorker.getLastTrackedObjects().add(new TrackedObject(object.getID(), msg.getTimeSent() + lidarWorker.getFrequency(), object.getDescription(), coordinates));
                }
                complete(msg,true);
            }

            else{
                lidarWorker.setStatus(STATUS.ERROR);
                complete(msg,false);
                crashTime = msg.getTimeSent();
                sendBroadcast(new CrashedBroadcast(getName(), msg.getTimeSent()));
                terminate();
                //send log
            }      
        });


        subscribeBroadcast(TickBroadcast.class, msg -> {
            int currentTick = msg.getTick();
            
            LinkedList<TrackedObject> trackedObjects = new LinkedList<TrackedObject>();
            Iterator<TrackedObject> iterator = lidarWorker.getLastTrackedObjects().iterator();
            while(iterator.hasNext()){
                TrackedObject object = iterator.next();
                if(object.getTime() <= currentTick){
                    trackedObjects.add(object);
                    iterator.remove();
                }
            }
            if(trackedObjects.size() > 0){
                sendEvent(new TrackedObjectsEvent(trackedObjects, currentTick));
                StatisticalFolder.getInstance().addNumTrackedObjects(trackedObjects.size());
            }

            if(lidarWorker.getDataBase().getCloudPointsSize() == 0 && lidarWorker.getLastTrackedObjects().size() == 0){
                lidarWorker.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
                //send log
            }
        });


        subscribeBroadcast(TerminatedBroadcast.class, msg -> {
            System.out.println(this.getName() + "recieved that " + msg.getSenderName() +  " terminated");
            if(msg.getSenderName() == "TimeService"){
                lidarWorker.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
                //send log
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, msg -> {
            System.out.println(this.getName() + "recieved that " + msg.getSenderName() +  " crashed");
            lidarWorker.setStatus(STATUS.DOWN);
            crashTime = msg.getCrashTime();
            sendBroadcast(new TerminatedBroadcast(getName()));
            terminate();  
            //send log          
        });
        
    }
}


