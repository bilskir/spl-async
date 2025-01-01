package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.application.messages.TrackedObjectsEvent;
/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    
    // Singleton instance holder
    private static class FusionSlamHolder {
        static final FusionSlam fs = new FusionSlam();  
    }

    private final List<LandMark> landmarks;
    private final List<Pose> poses;


    private FusionSlam(){
        this.landmarks = new LinkedList<LandMark>();
        this.poses = new ArrayList<Pose>();
        this.trackedObjects = new ArrayList<TrackedObject>();
    }

    public FusionSlam getInstance(){
        return FusionSlamHolder.fs;
    }

    public List<LandMark> getLandmarks() {
        return getInstance().landmarks;
    }

    public List<Pose> getPoses() {
        return getInstance().poses;
    }

}
