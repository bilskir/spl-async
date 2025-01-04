package bgu.spl.mics.application.objects;

/**
 * DetectedObject represents an object detected by the camera.
 * It contains information such as the object's ID and description.
 */
public class DetectedObject {

    private final String ID;
    private String description;

    public DetectedObject(String ID, String desc){
        this.ID =ID;
        this.description = desc;
    }

    public String getID(){
        return this.ID;
    }

    public String getDescription(){
        return this.description;
    }

    @Override
    public String toString() {
        return "DetectedObject{ID='" + ID + "', description='" + description + "'}";
    }
}
