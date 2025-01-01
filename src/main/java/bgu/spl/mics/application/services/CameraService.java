package bgu.spl.mics.application.services;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;
    private int currentTick;
    private final int duration;
    private StampedDetectedObjects[] detectedObjects;
    private int crashTime;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera,int duration) {
        super("CameraService");
        this.camera = camera;
        this.currentTick = 0;
        this.duration = duration;
        this.detectedObjects = new StampedDetectedObjects[duration + 1];
        this.crashTime = -1;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, msg -> {
            currentTick++;

            // If the current tick is greater than the duration, terminate the service.
            if(currentTick > duration){
                sendBroadcast(new TerminatedBroadcast(this.getName()));
                camera.setStatus(STATUS.DOWN);
                this.terminate();
            }

            else{

                // Binary search to find the index of the current tick in the stamps list.
                int i = binarySearch(0, camera.getStampsList().size() - 1, camera.getStampsList(), currentTick);
                if(i != -1){
                    StampedDetectedObjects objects = camera.getStampsList().get(i);
                    int index = currentTick + camera.getFrequency();
                   

                    // If the objects could be added to the LiDAR workers, store them in the detectedObjects array.
                    if (index < duration + 1) {
                        detectedObjects[index] = objects;
                    }
                
                    if(detectedObjects[currentTick] != null){
                         // If there are detected objects at the current tick and there is a error, send a CrashedBroadcast.
                        for(DetectedObject object : detectedObjects[currentTick].getDetectedObjectsList()){
                            if(object.getID() == "ERROR"){
                                camera.setStatus(STATUS.ERROR);
                                sendBroadcast(new CrashedBroadcast(this.getName(), currentTick));
                                terminate();
                                break;
                            }
                        }   

                        // If there is a error, send a CrashedBroadcast.
                        if(camera.getStatus() == STATUS.UP){
                            Future<Boolean> f =  this.sendEvent(new DetectObjectsEvent(detectedObjects[currentTick], currentTick));
                        }
                    }
                }
                
            }
            
            
        });
        

        subscribeBroadcast(TerminatedBroadcast.class, msg -> {
            System.out.println(this.getName() +  " recived that " + msg.getSenderName() + " got terminated.");
        });



        // Handle crashe of other sensors
        subscribeBroadcast(CrashedBroadcast.class, msg -> {
           crashTime = msg.getCrashTime();
           terminate();
           camera.setStatus(STATUS.DOWN);
           System.out.println(this.getName() +  " recived that " + msg.getSenderName() + " Crashed Bandicoot.");
           // TODO: Send log until crash time
           
        }); 
    }


    private int binarySearch(int l, int r, List<StampedDetectedObjects> myList, int target){
        int foundIndex = -1;
        while(l <= r && foundIndex == -1){
            int m = (l + r) / 2;
            int currentTime = camera.getStampsList().get(m).getTime();
            if(currentTime < target){
                l = m + 1;
            }
            else if (currentTime > target){
                r = m - 1;
            }
            else{
                foundIndex = m;
            }
        }
        return foundIndex;
    }
}






