package bgu.spl.mics.application.jsonClasses;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.Map;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OutputFile {
    private static OutputFile instance = new OutputFile("","");
    private String error;
    private String faultySensor;
    private final Map<String, LastFrameData> lastCamerasFrame;
    private final Map<String, List<TrackedObject>> lastLiDarWorkerTrackersFrame;
    private final List<Pose> poses;
    private final Statistics statistics;

    private OutputFile(String error, String faultySensor) {
        this.error = error;
        this.faultySensor = faultySensor;
        this.lastCamerasFrame = new ConcurrentHashMap<>();
        this.lastLiDarWorkerTrackersFrame = new ConcurrentHashMap<>();
        this.poses = new CopyOnWriteArrayList<>();
        this.statistics = new Statistics();
    }

    public static OutputFile getInstance() {
        return instance;
    }

    // Getters
    public String getError() {
        return error;
    }

    public String getFaultySensor() {
        return faultySensor;
    }

    public Map<String, LastFrameData> getLastCamerasFrame() {
        return new ConcurrentHashMap<>(lastCamerasFrame);
    }

    public Map<String, List<TrackedObject>> getLastLiDarWorkerTrackersFrame() {
        return new ConcurrentHashMap<>(lastLiDarWorkerTrackersFrame);
    }

    public List<Pose> getPoses() {
        return new CopyOnWriteArrayList<>(poses);
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setFaultySensor(String faultySensor) {
        this.faultySensor = faultySensor;
    }

    

    // Methods to update frames and poses
    public synchronized void addCameraFrame(String cameraId, StampedDetectedObjects frameData) {
        lastCamerasFrame.put(cameraId, new LastFrameData(frameData.getTime(), frameData.getDetectedObjectsList()));
    }

    public synchronized void addLiDarFrame(String workerId, List<TrackedObject> objects) {
        lastLiDarWorkerTrackersFrame.put(workerId, new CopyOnWriteArrayList<>(objects));
    }

    public synchronized void addPose(Pose pose) {
        poses.add(pose);
    }
}

class LastFrameData {
    private final int time;
    private final List<DetectedObject> detectedObjects;

    public LastFrameData(int time, List<DetectedObject> detectedObjects) {
        this.time = time;
        this.detectedObjects = new CopyOnWriteArrayList<>(detectedObjects);
    }

    public int getTime() {
        return time;
    }

    public List<DetectedObject> getDetectedObjects() {
        return new CopyOnWriteArrayList<>(detectedObjects);
    }
}


class Statistics {
    private final AtomicInteger systemRuntime;
    private final AtomicInteger numDetectedObjects;
    private final AtomicInteger numTrackedObjects;
    private final AtomicInteger numLandmarks;
    private final ConcurrentHashMap<String, LandMark> landMarks;


    public Statistics() {
        this.systemRuntime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
        this.landMarks = new ConcurrentHashMap<>();
    
    
    
    }
    public int getSystemRuntime() {
        return systemRuntime.get();
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    public int getNumLandmarks() {
        return numLandmarks.get();
    }

    public Map<String, LandMark> getLandMarks() {
        return new ConcurrentHashMap<>(landMarks);
    }

    public void incrementSystemRuntime(int increment) {
        systemRuntime.addAndGet(increment);
    }

    public void incrementNumDetectedObjects(int increment) {
        numDetectedObjects.addAndGet(increment);
    }

    public void incrementNumTrackedObjects(int increment) {
        numTrackedObjects.addAndGet(increment);
    }

    public void incrementNumLandmarks(int increment) {
        numLandmarks.addAndGet(increment);
    }

    public synchronized void updateLandMark(String id, LandMark newLandMark) {
        landMarks.put(id, newLandMark);
        incrementNumLandmarks(1);
    }

    public synchronized void removeLandMark(String id) {
        if (landMarks.remove(id) != null) {
            incrementNumLandmarks(-1);
        }
    }

    public LandMark getLandMark(String id) {
        return landMarks.get(id);
    }
}

