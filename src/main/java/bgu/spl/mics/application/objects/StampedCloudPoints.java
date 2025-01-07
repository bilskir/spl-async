package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    @SerializedName("id")
    private final String ID;

    private final int time;
    private final List<List<Double>> cloudPoints;  // Updated to List of Lists

    public StampedCloudPoints(String ID, int time, List<List<Double>> cloudPoints) {
        this.ID = ID;
        this.time = time;
        this.cloudPoints = cloudPoints;
    }

    public String getID() {
        return ID;
    }

    public int getTime() {
        return time;
    }

    public List<List<Double>> getCloudPoints() {
        return cloudPoints;
    }

    /**
     * Converts the list of cloud points to a list of CloudPoint objects.
     * 
     * @return List of CloudPoint objects.
     */
    public List<CloudPoint> toCloudPointList() {
        return cloudPoints.stream()
                .map(point -> new CloudPoint(point.get(0), point.get(1)))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "StampedCloudPoints{ID='" + ID + "', time=" + time + "CloudPointsSize: " + cloudPoints.size() + "}";
    }
}
