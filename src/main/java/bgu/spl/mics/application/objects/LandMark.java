package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;
/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private final String ID;
    private final String description;
    private List<CloudPoint> coordinates;

    public LandMark(String ID, String desc){
        this.ID = ID;
        this.description = desc;
        this.coordinates = new LinkedList<CloudPoint>();
    }

    public LandMark(String ID, String desc, List<CloudPoint> coordinates){
        this.ID = ID;
        this.description = desc;
        this.coordinates = coordinates;
    }

    public String getID(){
        return this.ID;
    }

    public String getDescription(){
        return this.description;
    }

    public List<CloudPoint> getCoordinates(){
        return this.coordinates;
    }
}

/*s:
o Id: String – the internal ID of the object.
o Description: String – the description of the landmark.
o Coordinates: List of CloudPoints – list of coordinates of the object according to the
charging station’s coordinate system. */