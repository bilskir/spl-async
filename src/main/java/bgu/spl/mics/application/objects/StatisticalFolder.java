package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private static StatisticalFolder sf = new StatisticalFolder();
    private int systemRuntime;
    private int numTrackedObjects;
    private int numDetectedObjects;
    private int numLandmarks;

    private StatisticalFolder(){
        this.systemRuntime = 0;
        this.numDetectedObjects = 0;
        this.numTrackedObjects = 0;
        this.numLandmarks = 0;
    }

    public static StatisticalFolder getInstance(){

        return sf;
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects;
    }

    public int getNumLandmarks() {
        return numLandmarks;
    }

    public int getSystemRuntime() {
        return systemRuntime;
    }
    
    public int getNumTrackedObjects() {
        return numTrackedObjects;
    }


    public synchronized void addNumDetectedObjects(int num) {
        numDetectedObjects += num;
    }

    public synchronized void addNumLandmarks(int num) {
        numLandmarks += num;
    }

    public synchronized void addSystemRuntime(int num) {
        systemRuntime += num;
    }
    
    public synchronized void addNumTrackedObjects(int num) {
        numTrackedObjects += num;
    }
}
