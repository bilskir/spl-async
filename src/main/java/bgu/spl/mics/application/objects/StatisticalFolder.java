package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private static StatisticalFolder sf = null;
    private int systemRuntime;
    private int numTrackedObjects;
    private int numDetectedObjects;
    private int numLandmarks;

    private StatisticalFolder(){
        this.systemRuntime = 0;
        this.numDetectedObjects = 0;
        this.numDetectedObjects = 0;
        this.numLandmarks = 0;
    }

    public StatisticalFolder getInstance(){
        if(sf == null){
            sf = new StatisticalFolder();
        }

        return sf;
    }

    public int getNumDetectedObjects() {
        return getInstance().numDetectedObjects;
    }

    public int getNumLandmarks() {
        return getInstance().numLandmarks;
    }

    public int getSystemRuntime() {
        return getInstance().systemRuntime;
    }
    
    public int getNumTrackedObjects() {
        return getInstance().numTrackedObjects;
    }

}
