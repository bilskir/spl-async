package bgu.spl.mics.application.objects;
import java.util.List;
import java.util.LinkedList;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private final int time;
    private final List<DetectedObject> detectedObjects;

    public StampedDetectedObjects(int time){
        this.time = time;
        detectedObjects = new LinkedList<DetectedObject>();
    }

    public StampedDetectedObjects(int time, List<DetectedObject> detectedObjects){
        this.time = time;
        this.detectedObjects = detectedObjects;
    }

    public int getTime(){
        return this.time;
    }

    public List<DetectedObject> getDetectedObjectsList(){
        return this.detectedObjects;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StampedDetectedObjects{time=").append(time);
        sb.append(", detectedObjects=[");
        for (DetectedObject obj : detectedObjects) {
            sb.append(obj.toString()).append(", ");
        }
        if (!detectedObjects.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove the last comma and space
        }
        sb.append("]}");
        return sb.toString();
    }
}
