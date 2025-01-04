package bgu.spl.mics.application.objects;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick; 
    private STATUS status;
    private final List<Pose> poseList;
   

    public GPSIMU(int currentTick){
        this.currentTick = currentTick;
        status = STATUS.UP;
        poseList = new LinkedList<Pose>();
    }

    public STATUS getStatus(){
        return this.status;
    }

    public void setStatus(STATUS newStatus){
        status = newStatus;
    }

    public int getCurrentTick(){
        return this.currentTick;
    }
    
    public List<Pose> getPoseList(){
        return this.poseList;
    }
}
