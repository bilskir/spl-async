package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.mics.application.messages.TrackedObjectsEvent;
/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    
    private final List<LandMark> landmarks;
    private final List<Pose> poses;
    private final ConcurrentHashMap<Integer, Object[]> map;

    // Singleton instance holder
    private static class FusionSlamHolder {
        static final FusionSlam fs = new FusionSlam();  
    }    

    private FusionSlam(){
        this.landmarks = new LinkedList<LandMark>();
        this.poses = new ArrayList<Pose>();
        this.map = new ConcurrentHashMap<Integer,Object[]>();
    }

    public static FusionSlam getInstance(){
        return FusionSlamHolder.fs;
    }

    public ConcurrentHashMap<Integer, Object[]> getMap(){
        return map;
     
    }

    public List<LandMark> getLandmarks() {
        return getInstance().landmarks;
    }

    public List<Pose> getPoses() {
        return getInstance().poses;
    }

    /*
     * Add new pose to the global map.
     * 
     * @param pose The new pose to add at corrrent time.
     * 
     */
    public void addPose(Pose pose){ 
        if(map.get(pose.getTime()) == null){
            Object[] objects = new Object[2];
            objects[0] = pose;
            objects[1] = new LinkedList<TrackedObject>();
            map.put(pose.getTime(), objects);
        }

        else{
            Object[] objects = map.get(pose.getTime());
            objects[0] = pose;
            map.put(pose.getTime(), objects);
        }
    }

    /*
     * Add new tracked object to the global map.
     * 
     * @param trackedObject The new tracked object to add at corrrent time.
     * 
     */
    public void addTrackedObject(TrackedObject trackedObject){
        if(map.get(trackedObject.getTime()) == null){
            Object[] objects = new Object[2];
            LinkedList<TrackedObject> trackedObjects = new LinkedList<TrackedObject>();
            trackedObjects.add(trackedObject);
            objects[1] = trackedObjects;
            map.put(trackedObject.getTime(), objects);
        }

        else{
            Object[] objects = map.get(trackedObject.getTime());
            LinkedList<TrackedObject> trackedObjects = (LinkedList<TrackedObject>) objects[1];
            trackedObjects.add(trackedObject);
            objects[1] = trackedObjects;
            map.put(trackedObject.getTime(), objects);
        }
    }

    /*
     * Calculate the global map at the current time.
     * 
     * @param time The current time to calculate the map.
     * 
     */
    public void calculateMap(int time){
        for (ConcurrentHashMap.Entry<Integer, Object[]> entry : map.entrySet()) {
            if(entry.getKey() <= time){
                if(entry.getValue() != null && entry.getValue()[0] != null && entry.getValue()[1] != null){
                    for(TrackedObject object : (LinkedList<TrackedObject>) entry.getValue()[1]){
                        if(object.getCoordinates() != null){
                            calculatePosition(object, (Pose) entry.getValue()[0]);
                        }
                    }

                    entry.getValue()[1] = new LinkedList<TrackedObject>();
                }
            }
        }
    }

    /**
     * Calculates the position of a tracked object and stores it as a landmark.
     *
     * @param object The tracked object to process.
     * @param pose   The robot's current pose.
     */
    public void calculatePosition(TrackedObject object, Pose pose) {
        // List to hold transformed global coordinates
        List<CloudPoint> globalCoordinates = new LinkedList<>();
    
        // Transform each coordinate of the tracked object
        for (CloudPoint localPoint : object.getCoordinates()) {
            double[] global = transformToGlobal(localPoint.getX(), localPoint.getY(), pose);
            globalCoordinates.add(new CloudPoint(global[0], global[1]));
        }
    
        // Check if a landmark with the same ID already exists
        for (LandMark landmark : landmarks) {
            if (landmark.getID().equals(object.getID())) {
                // Merge coordinates by averaging
                List<CloudPoint> existingCoordinates = landmark.getCoordinates();
                for (int i = 0; i < Math.min(existingCoordinates.size(), globalCoordinates.size()); i++) {
                    double newX = (existingCoordinates.get(i).getX() + globalCoordinates.get(i).getX()) / 2;
                    double newY = (existingCoordinates.get(i).getY() + globalCoordinates.get(i).getY()) / 2;
                    existingCoordinates.get(i).setX(newX);
                    existingCoordinates.get(i).setY(newY);
                }
                return;  // Exit after merging
            }
        }
    
        // If the landmark is new, add it
        LandMark newLandmark = new LandMark(object.getID(), object.getDescription(), globalCoordinates);
        landmarks.add(newLandmark);
        StatisticalFolder.getInstance().addNumLandmarks(1);
    }
    

    /**
     * Transforms local coordinates to global coordinates based on a given pose.
     *
     * @param localX  the x-coordinate in the local frame
     * @param localY  the y-coordinate in the local frame
     * @param pose    the robot's pose containing global position and orientation
     * @return a double array containing the global x and y coordinates
     */
    public double[] transformToGlobal(double localX, double localY, Pose pose) {
        double yawRad = Math.toRadians(pose.getYaw());
        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);

        double globalX = cosYaw * localX - sinYaw * localY + pose.getX();
        double globalY = sinYaw * localX + cosYaw * localY + pose.getY();

        return new double[]{globalX, globalY};
    }

    @Override
    public String toString() {
        return "FusionSlam{landmarks=" + landmarks + ", map=" + map + "}";
    }
}
     