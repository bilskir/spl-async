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
    private final LiDarDataBase dataBase;


    public LiDarWorkerTracker(int ID, int frequency, String filePath){
        this.ID = ID;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.lastTrackedObjects = new LinkedList<TrackedObject>();
        this.dataBase = LiDarDataBase.getInstance(filePath);
    }

    public LiDarWorkerTracker(int ID, int frequency, List<TrackedObject> trackedObjects, LiDarDataBase dataBase){
        this.ID = ID;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.lastTrackedObjects = trackedObjects;
        this.dataBase = dataBase;
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

    public LiDarDataBase getDataBase() {
        return dataBase;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }


    public boolean isError(int time){
        for(StampedCloudPoints cp : dataBase.getCloudPoints()){
            if(cp.getTime() == time){
                if(cp.getID() == "ERROR"){
                    return true;
                }
            }

            if(cp.getTime() > time){
                return false;
            }
        }

        return false;
    }

    public List<CloudPoint> getCoordinates(DetectedObject object, int time){
        for(StampedCloudPoints cp : dataBase.getCloudPoints()){
            if(cp.getID() == object.getID() && cp.getTime() == time){
                return cp.getCloudPoints();
            }
        }

        return null;
    }
    
}
