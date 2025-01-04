package bgu.spl.mics.application.objects;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick; 
    private STATUS status;
    private final List<Pose> poseList;
   

    public GPSIMU(String pathString){
         this.currentTick = 0;
        this.status = STATUS.UP;
        this.poseList = new LinkedList<>();

        // Parse the JSON file containing pose data
        try (FileReader reader = new FileReader(pathString)) {
            Gson gson = new Gson();
            Pose[] poses = gson.fromJson(reader, Pose[].class);
            for (Pose pose : poses) {
                poseList.add(pose);
            }

        } catch (IOException e) {
            System.err.println("Failed to read pose data from: " + pathString);
            e.printStackTrace();
        }
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

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GPSIMU{currentTick=").append(currentTick);
        sb.append(", status=").append(status);
        sb.append(", poseList=[");
        for (Pose pose : poseList) {
            sb.append(pose.toString()).append(", ");
        }
        if (!poseList.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove the last comma and space
        }
        sb.append("]}");
        return sb.toString();
    }
}
