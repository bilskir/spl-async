package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.StampedDetectedObjects;



public class DetectObjectsEvent implements Event<Boolean> {
    private final StampedDetectedObjects objects;
    private final int timeSent;
    
    public DetectObjectsEvent(StampedDetectedObjects objects, int timeSent) {
        this.objects = objects;
        this.timeSent = timeSent;
    }

    public StampedDetectedObjects getStampedDetectedObjects() {
        return objects;
    }

    public int getTimeSent() {
        return timeSent;
    }
}
