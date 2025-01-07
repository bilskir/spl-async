package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
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
    private int crashTime;
    private int numberOfObjects;


    /*
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super(camera.getCameraKey());
        this.camera = camera;
        this.crashTime = -1;
        this.numberOfObjects = 0;
        
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for
     * sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, msg -> {
            int currentTick = msg.getTick();

            
            //System.out.println(this.getName() + " recieved tick " + currentTick);
            int index = binarySearch(0, camera.getStampsList().size() - 1, camera.getStampsList(), currentTick);

            if (index != -1) {
                if (camera.checkForError(index)) {
                    camera.setStatus(STATUS.ERROR);
                    sendBroadcast(new CrashedBroadcast(getName(), currentTick));
                    terminate();
                } else {
                    //numberOfObjects += camera.getStampsList().get(index).getDetectedObjectsList().size();
                    //System.out.println(camera.getStampsList().get(index).getDetectedObjectsList().size());
                    
                    camera.addEvent(new DetectObjectsEvent(camera.getStampsList().get(index), currentTick),
                    currentTick);
                    if (camera.getEvent(currentTick) != null) {

                        int counter = camera.getEvent(currentTick).getStampedDetectedObjects().getDetectedObjectsList().size();
                        Future<Boolean> f = sendEvent(camera.getEvent(currentTick)); 
                        StatisticalFolder.getInstance().addNumDetectedObjects(counter);
                        StampedDetectedObjects object = camera.getEvent(currentTick).getStampedDetectedObjects();
                        camera.setLastFrame(object);
                        
                        //System.out.println("detectedObjectsCounter is: " + detectedObjectsCounter);
                        //int detectedObjectsCounter = camera.getStampsList().get(index).getDetectedObjectsList().size();
                    }
                }
            }
            if (currentTick >= camera.getCameraDuration()) {
                System.out.println(this.getName() + " is done");
                camera.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
                // send log
            }

        });

        subscribeBroadcast(TerminatedBroadcast.class, msg -> {
            System.out.println(this.getName() + "recieved that " + msg.getSenderName() + " terminated");
            if (msg.getSenderName() == "TimeService") {
                camera.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();
                // send log
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, msg -> {
            System.out.println(this.getName() + "recieved that " + msg.getSenderName() + " crashed");
            camera.setStatus(STATUS.DOWN);
            crashTime = msg.getCrashTime();
            sendBroadcast(new TerminatedBroadcast(getName()));
            terminate();
            // send log
        });
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CameraService{");
        sb.append("camera=").append(camera);
        sb.append(", crashTime=").append(crashTime);
        sb.append(", serviceName=").append(getName());
        sb.append("}");
        return sb.toString();
    }

    public Camera getCamera() {
        return camera;
    }



    private int binarySearch(int l, int r, List<StampedDetectedObjects> myList, int target) {
        int foundIndex = -1;
        while (l <= r && foundIndex == -1) {
            int m = (l + r) / 2;
            int currentTime = camera.getStampsList().get(m).getTime();
            if (currentTime < target) {
                l = m + 1;
            } else if (currentTime > target) {
                r = m - 1;
            } else {
                foundIndex = m;
            }
        }
        return foundIndex;
    }
}
