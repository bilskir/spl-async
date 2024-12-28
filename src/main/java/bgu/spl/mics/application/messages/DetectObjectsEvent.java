package bgu.spl.mics.application.messages;
import java.util.List;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;


public class DetectObjectsEvent implements Event<Boolean> {
    private final List<DetectedObject> objects;
    

    public DetectObjectsEvent(List<DetectedObject> objects) {
        this.objects = objects;
    }

    public List<DetectedObject> getStampedDetectedObjects() {
        return objects;
    }
}