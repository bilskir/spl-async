package bgu.spl.mics.application.jsonClasses;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Configuration {
    @SerializedName("Cameras")
    private Cameras cameras;

    @SerializedName("LiDarWorkers")
    private Lidars lidars;

    @SerializedName("poseJsonFile")
    private String poseJsonFile;

    @SerializedName("TickTime")
    private int tickTime;

    @SerializedName("Duration")
    private int duration;

    // Getters and Setters
    public Cameras getCameras() {
        return cameras;
    }

    public void setCameras(Cameras cameras) {
        this.cameras = cameras;
    }

    public Lidars getLidars() {
        return lidars;
    }

    public void setLidars(Lidars lidars) {
        this.lidars = lidars;
    }

    public String getPoseJsonFile() {
        return poseJsonFile;
    }

    public void setPoseJsonFile(String poseJsonFile) {
        this.poseJsonFile = poseJsonFile;
    }

    public int getTickTime() {
        return tickTime;
    }

    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }

    public int getDuration() {
        return duration;
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static class Cameras {
        @SerializedName("CamerasConfigurations")
        private List<CameraConfiguration> camerasConfigurations;

        @SerializedName("camera_datas_path")
        private String cameraDatasPath;

        // Getters and Setters
        public List<CameraConfiguration> getCamerasConfigurations() {
            return camerasConfigurations;
        }

        public void setCamerasConfigurations(List<CameraConfiguration> camerasConfigurations) {
            this.camerasConfigurations = camerasConfigurations;
        }

        public String getCameraDatasPath() {
            return cameraDatasPath;
        }

        public void setCameraDatasPath(String cameraDatasPath) {
            this.cameraDatasPath = cameraDatasPath;
        }
    }

    public static class CameraConfiguration {
        private int id;
        private int frequency;

        @SerializedName("camera_key")
        private String cameraKey;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        public String getCameraKey() {
            return cameraKey;
        }

        public void setCameraKey(String cameraKey) {
            this.cameraKey = cameraKey;
        }
    }

    public static class Lidars {
        @SerializedName("LidarConfigurations")
        private List<LidarConfiguration> lidarConfigurations;

        @SerializedName("lidars_data_path")
        private String lidarsDataPath;

        // Getters and Setters
        public List<LidarConfiguration> getLidarConfigurations() {
            return lidarConfigurations;
        }

        public void setLidarConfigurations(List<LidarConfiguration> lidarConfigurations) {
            this.lidarConfigurations = lidarConfigurations;
        }

        public String getLidarsDataPath() {
            return lidarsDataPath;
        }

        public void setLidarsDataPath(String lidarsDataPath) {
            this.lidarsDataPath = lidarsDataPath;
        }
    }

    public static class LidarConfiguration {
        private int id;
        private int frequency;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
    }
}
