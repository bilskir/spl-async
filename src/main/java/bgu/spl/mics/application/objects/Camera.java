package bgu.spl.mics.application.objects;
import java.util.List;

import bgu.spl.mics.application.messages.DetectObjectsEvent;

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
    private DetectObjectsEvent[] detectObjectsEvents;
    private final int cameraDuration;



    public Camera(int ID, int frequency){
        this.ID = ID;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.stampsList = new ArrayList<StampedDetectedObjects>();
        this.cameraDuration = stampsList.get(stampsList.size() - 1).getTime() + frequency;
        detectObjectsEvents = new DetectObjectsEvent[cameraDuration + 1];
    }

    public Camera(int ID,int frequency, List<StampedDetectedObjects> stamps){
        this.ID = ID;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.stampsList = stamps;
        this.cameraDuration = stampsList.get(stampsList.size() - 1).getTime() + frequency;
        detectObjectsEvents = new DetectObjectsEvent[cameraDuration + 1];
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

    public int getCameraDuration() {
        return cameraDuration;
    }
    
    public void setStatus(STATUS newStatus){
        status = newStatus;
    }

    public List<StampedDetectedObjects> getStampsList(){
        return this.stampsList;
    }

    /*
    * When getting a tick add event to coressponding execution time (tick + camera frequency)
    * 
    * @param event - the event to add from the JSON file 
    * @param tick - the current tick
    * @return true if the event was added successfully, false otherwise
    *
    */
    public boolean addEvent(DetectObjectsEvent event, int tick){
        if(tick + frequency <= cameraDuration){
            detectObjectsEvents[tick + frequency + 1] = event;
            return true;
        }

        return false;
    }


    /*
     * 
     * Check for error at current tick 
     * 
     * @param tick - the current tick
     * @return true if there is an error, false otherwise
     * 
     * 
     */
    public boolean checkForError(int tick){
        for(DetectedObject object : stampsList.get(tick).getDetectedObjectsList()){
           if(object.getID() == "ERROR"){
               return true;
           }
        }

        return false;
    }

    /*
     * 
     * get the event at the current tick
     * 
     * @param tick - the current tick
     * 
     */
    public DetectObjectsEvent getEvent(int tick){
        return detectObjectsEvents[tick];
    }


    
}
