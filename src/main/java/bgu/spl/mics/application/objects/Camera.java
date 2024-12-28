package bgu.spl.mics.application.objects;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final int ID;
    private final int frequency;
    private STATUS status;
    private final List<StampedDetectedObjects> stampsList;



    public Camera(int ID, int frequency){
        this.ID = ID;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.stampsList = new ArrayList<StampedDetectedObjects>();
    }

    public Camera(int ID,int frequency, List<StampedDetectedObjects> stamps){
        this.ID = ID;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.stampsList = stamps;
    }


    public int getID(){
        return this.ID;
    }

    public int getFrequency(){
        return this.frequency;
    }

    public STATUS getStatus(){
        return this.status;
    }

    public void setStatus(STATUS newStatus){
        status = newStatus;
    }

    public List<StampedDetectedObjects> getStampsList(){
        return this.stampsList;
    }


    
}
