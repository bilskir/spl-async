package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;
import javax.sound.midi.Track;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private final String ID;
    private final int time;
    private String description;
    private List<CloudPoint> coordinates;

    public TrackedObject(String ID, int time, String desc){
        this.ID = ID;
        this.time = time;
        this.description = desc;
        this.coordinates = new LinkedList<CloudPoint>();
    }

    public TrackedObject(String ID, int time, String desc,List<CloudPoint> cord){
        this.ID = ID;
        this.time = time;
        this.description = desc;
        this.coordinates = cord;

    }

    public String getID() {
        return ID;
    }

    public int getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }
    
    public List<CloudPoint> getCoordinates() {
        return coordinates;
    }
}
