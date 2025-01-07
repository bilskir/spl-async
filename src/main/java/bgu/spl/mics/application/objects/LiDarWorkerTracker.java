package bgu.spl.mics.application.objects;

import java.util.List;

import java.util.Iterator;

import java.util.LinkedList;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using
 * data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    private final int ID;
    private final int frequency;
    private STATUS status;
    private final List<TrackedObject> lastTrackedObjects;
    private final LiDarDataBase dataBase;
    private String errorDesc;

    public LiDarWorkerTracker(int ID, int frequency, String filePath) {
        this.ID = ID;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.lastTrackedObjects = new LinkedList<TrackedObject>();
        this.dataBase = LiDarDataBase.getInstance(filePath);
        this.errorDesc = "";
    }

    public LiDarWorkerTracker(int ID, int frequency, LiDarDataBase dataBase) {
        this.ID = ID;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.lastTrackedObjects = new LinkedList<TrackedObject>();
        this.dataBase = dataBase;
        this.errorDesc = "";
    }

    public int getID() {
        return ID;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getErrorDesc() {
        return errorDesc;
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

    public boolean isError(int time) {

        Iterator<StampedCloudPoints> iterator = dataBase.getCloudPoints().iterator();
        while (iterator.hasNext()) {
            StampedCloudPoints cp = iterator.next();
            if (cp.getTime() == time && "ERROR".equals(cp.getID())) {
                errorDesc = cp.getID();
                return true;
            }
            if (cp.getTime() > time) {
                return false;
            }
        }

        return false;
    }

    public List<CloudPoint> getCoordinates(DetectedObject object, int time) {
        for (StampedCloudPoints cp : dataBase.getCloudPoints()) {
            if (cp.getID().equals(object.getID()) && cp.getTime() == time) {
                return cp.toCloudPointList();
            }
        }

        return null;
    }

    public String toString() {
        return "LiDarWorkerTracker{ID=" + ID + ", frequency=" + frequency + ", status=" + status
                + ", lastTrackedObjects=" + lastTrackedObjects + ", cloudPointsSize=" + dataBase.getCloudPointsSize()
                + "}";
    }

}
