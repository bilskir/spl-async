package bgu.spl.mics.application.jsonClasses;

import java.util.LinkedList;
import java.util.List;

public class CamerasConfigurations {
    List<CameraConfiguration> cameras;

    public CamerasConfigurations() {
        cameras = new LinkedList<CameraConfiguration>();       

    }

    public List<CameraConfiguration> getCameras() {
        return cameras;
    }

    public void addCamera(CameraConfiguration camera){
        this.cameras.add(camera);
    }
}
