package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;
/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private final String ID;
    private final int time;
    private final List<CloudPoint> cloudPoints;

    public StampedCloudPoints(String ID, int time){
        this.ID = ID;
        this.time = time;
        this.cloudPoints = new LinkedList<CloudPoint>();
    }

    public StampedCloudPoints(String ID, int time,List<CloudPoint> cloudPoints){
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
    
    public List<CloudPoint> getCloudPoints() {
        return cloudPoints;
    }
}
