package bgu.spl.mics.application.services;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
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
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService");
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, msg -> {
                // Binary Search
                int currentTick = binarySearch(0, camera.getStampsList().size() - 1, camera.getStampsList(), msg.getTickNumber());
                
                //subsribe to the 3

                if(currentTick != -1){
                    List<DetectedObject> objects = camera.getStampsList().get(currentTick).getDetectedObjectsList();
                    Thread.sleep(camera.getFrequency());
                    sendEvent(new DetectObjectsEvent(objects));
                }
        });
    }

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


