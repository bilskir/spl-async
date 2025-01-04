package bgu.spl.mics.application.jsonClasses;
import java.util.List;

public class Configuration {
    public Cameras Cameras;
    public LidarWorkers LidarWorkers;
    public String poseJsonFile;
    public int TickTime;
    public int Duration;

    public Cameras getCameras() {
        return Cameras;
    }
    public LidarWorkers getLidarWorkers() {
        return LidarWorkers;
    }
    public String getPoseJsonFile() {
        return poseJsonFile;
    }
    public int getDuration() {
        return Duration;
    }
    public int getTickTime() {
        return TickTime;
    }
    public void setCameras(Cameras cameras) {
        Cameras = cameras;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public void setLidarWorkers(LidarWorkers lidarWorkers) {
        LidarWorkers = lidarWorkers;
    }

    public void setPoseJsonFile(String poseJsonFile) {
        this.poseJsonFile = poseJsonFile;
    }

    public void setTickTime(int tickTime) {
        TickTime = tickTime;
    }
    public static class Cameras {
        public List<CameraConfiguration> CamerasConfigurations;
        public String camera_datas_path;

        public String getCamera_datas_path() {
            return camera_datas_path;
        }
        public List<CameraConfiguration> getCamerasConfigurations() {
            return CamerasConfigurations;
        }

        public void setCamera_datas_path(String camera_datas_path) {
            this.camera_datas_path = camera_datas_path;
        }
        public void setCamerasConfigurations(List<CameraConfiguration> camerasConfigurations) {
            CamerasConfigurations = camerasConfigurations;
        }
    }

    public static class CameraConfiguration {
        public int id;
        public int frequency;
        public String camera_key;

        public String getCamera_key() {
            return camera_key;
        }
        public int getFrequency() {
            return frequency;
        }
        public int getId() {
            return id;
        }
        public void setCamera_key(String camera_key) {
            this.camera_key = camera_key;
        }
        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
        public void setId(int id) {
            this.id = id;
        }
    }

    public static class LidarWorkers {
        public List<LidarConfiguration> LidarConfigurations;
        public String lidars_data_path;

        public List<LidarConfiguration> getLidarConfigurations() {
            return LidarConfigurations;
        }
        public String getLidars_data_path() {
            return lidars_data_path;
        }
        public void setLidarConfigurations(List<LidarConfiguration> lidarConfigurations) {
            LidarConfigurations = lidarConfigurations;
        }
        public void setLidars_data_path(String lidars_data_path) {
            this.lidars_data_path = lidars_data_path;
        }
    }

    public static class LidarConfiguration {
        public int id;
        public int frequency;
        public int getFrequency() {
            return frequency;
        }
        public int getId() {
            return id;
        }
        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
        public void setId(int id) {
            this.id = id;
        }
    }
}