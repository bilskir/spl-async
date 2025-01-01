package bgu.spl.mics.application.messages;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.Event;

/**
 * PoseEvent is an event that provides the robot's current pose.
 * Sent by: PoseService
 * Handled by: Fusion-SLAM
 */
public class PoseEvent implements Event<Void> {

    private final Pose currentPose;
    

    /**
     * Constructor for PoseEvent.
     *
     * @param currentPose The current pose of the robot.
     */
    public PoseEvent(Pose currentPose) {
        this.currentPose = currentPose;
    }

    /**
     * Gets the current pose of the robot.
     *
     * @return The current pose.
     */
    public Pose getCurrentPose() {
        return currentPose;
    }
}