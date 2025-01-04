package bgu.spl.mics.application.jsonClasses;

public class CameraConfiguration {
    private int id;
    private int frequency;
    private String cameraKey;

    public CameraConfiguration(int id, int frequncy, String cameraKey) {
        this.id = id;
        this.frequency = frequncy;
        this.cameraKey = cameraKey;
    }

    public String getCameraKey() {
        return cameraKey;
    }
    
    public int getFrequency() {
        return frequency;
    }

    public int getId() {
        return id;
    }
    
}
