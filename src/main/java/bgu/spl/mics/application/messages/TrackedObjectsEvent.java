
package bgu.spl.mics.application.messages;

import java.util.List;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

/**
 * TrackedObjectsEvent is an event that contains a list of tracked objects.
 * Sent by: LiDAR Worker
 * Handled by: Fusion-SLAM
 */
public class TrackedObjectsEvent implements Event<Boolean> {

    private final List<TrackedObject> TrackedObjects;
    private final int timeSent;

    /**
     * Constructor for TrackedObjectsEvent.
     *
     * @param trackedObjects The list of tracked objects to include in this event.
     */
    public TrackedObjectsEvent(List<TrackedObject> trackedObjects, int timeSent) {
        this.TrackedObjects = trackedObjects;
        this.timeSent = timeSent;
    }

    /**
     * Gets the list of tracked objects included in this event.
     *
     * @return The list of tracked objects.
     */
    public List<TrackedObject> getTrackedObjects() {
        return TrackedObjects;
    }

    public int getTimeSent() {
        return timeSent;
    }
}