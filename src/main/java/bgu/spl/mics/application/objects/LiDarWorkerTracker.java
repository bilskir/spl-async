package bgu.spl.mics.application.objects;
import java.util.List;
import java.util.LinkedList;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    private final int ID;
    private final int frequency;
    private STATUS status;
    private final List<TrackedObject> lastTrackedObjects;


    public LiDarWorkerTracker(int ID, int frequency){
        this.ID = ID;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.lastTrackedObjects = new LinkedList<TrackedObject>();
    }

    public LiDarWorkerTracker(int ID, int frequency, List<TrackedObject> trackedObjects){
        this.ID = ID;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.lastTrackedObjects = trackedObjects;
    }
    
    public int getID() {
        return ID;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }

    public STATUS getStatus() {
        return status;
    }

    
}




/*
 * o id: int – The ID of the LiDar.
o frequency: int – The time interval at which the LiDar sends new events (If the time in the
Objects is 2 then the LiDarWorker sends it at time 2 + Frequency).
o status: enum – Up, Down, Error.
o lastTrackedObjects: List of TrackedObject – The last objects the LiDar tracked.
 */