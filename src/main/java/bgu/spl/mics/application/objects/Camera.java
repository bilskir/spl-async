package bgu.spl.mics.application.objects;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import bgu.spl.mics.application.messages.DetectObjectsEvent;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final int ID;
    private final int frequency;
    private final String cameraKey;
    private STATUS status;
    private final List<StampedDetectedObjects> stampsList;
    private DetectObjectsEvent[] detectObjectsEvents;
    private final int cameraDuration;
    private String errorDescription;
    private StampedDetectedObjects lastFrame;

    public Camera(int ID, int frequency, String pathString, String cameraKey) {
        this.ID = ID;
        this.frequency = frequency;
        this.cameraKey = cameraKey;
        this.status = STATUS.UP;
        this.stampsList = new ArrayList<>();
        this.errorDescription = "";
        lastFrame = null;
    
        // Parse the JSON file containing stamped detected objects
        try (FileReader reader = new FileReader(pathString)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
    
            if (jsonObject.has(cameraKey)) {
                // Parse a flat list of StampedDetectedObjects
                List<StampedDetectedObjects> stampedObjects = gson.fromJson(
                    jsonObject.getAsJsonArray(cameraKey),
                    new TypeToken<List<StampedDetectedObjects>>() {}.getType()
                );
                this.stampsList.addAll(stampedObjects);
            } else {
                System.err.println("Camera key not found in the provided JSON: " + cameraKey);
            }
        } catch (IOException e) {
            System.err.println("Failed to read camera data from: " + pathString);
            e.printStackTrace();
        }
    
        this.cameraDuration = stampsList.isEmpty() ? 0 : stampsList.get(stampsList.size() - 1).getTime() + frequency;
        this.detectObjectsEvents = new DetectObjectsEvent[cameraDuration + 1];
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

    public String getCameraKey() {
        return cameraKey;
    }

    public  void setLastFrame(StampedDetectedObjects lastFrame) {
        this.lastFrame = new StampedDetectedObjects(lastFrame.getTime(),lastFrame.getDetectedObjectsList());

    }

    public StampedDetectedObjects getLastFrame() {
        return lastFrame;
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
            detectObjectsEvents[tick + frequency] = event;
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
            if(object.getID().equals("ERROR")){
                errorDescription = object.getDescription();      
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
    public DetectObjectsEvent getEvent(int tick) {
        if (detectObjectsEvents[tick] != null) {
            return detectObjectsEvents[tick];
        }
        return null;
    }
    
    public String getErrorDescription() {
        return errorDescription;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Camera{ID=").append(ID);
        sb.append(", frequency=").append(frequency);
        sb.append(", status=").append(status);
        sb.append(", cameraDuration=").append(cameraDuration);
        sb.append(", stampsList=[");
        for (StampedDetectedObjects stamped : stampsList) {
            sb.append(stamped.toString()).append(", ");
        }
        if (!stampsList.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove the last comma and space
        }
        sb.append("]}");
         return sb.toString();
    }
}
