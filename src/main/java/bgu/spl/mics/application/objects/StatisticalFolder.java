package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {

    // Singleton instance
    private static final StatisticalFolder sf = new StatisticalFolder();

    // Using AtomicInteger for thread-safe counters
    private final AtomicInteger systemRuntime;
    private final AtomicInteger numTrackedObjects;
    private final AtomicInteger numDetectedObjects;
    private final AtomicInteger numLandmarks;

    private StatisticalFolder() {
        this.systemRuntime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
    }

    // Singleton getter
    public static StatisticalFolder getInstance() {
        return sf;
    }

    // Getters for counters
    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    public int getNumLandmarks() {
        return numLandmarks.get();
    }

    public int getSystemRuntime() {
        return systemRuntime.get();
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    // Methods to increment counters
    public void addNumDetectedObjects(int num) {
        int oldValue;
        do {
            oldValue = getNumDetectedObjects();
        } while (!numDetectedObjects.compareAndSet(oldValue, oldValue + num));
    }

    public void addNumLandmarks(int num) {
        int oldValue;
        do {
            oldValue = getNumLandmarks();
        } while (!numLandmarks.compareAndSet(oldValue, oldValue + num));
    }

    public void addSystemRuntime(int num) {
        int oldValue; 
        do{
            oldValue = getSystemRuntime();
        }
        while(!systemRuntime.weakCompareAndSet(oldValue, oldValue + num));
    }

    public void addNumTrackedObjects(int num) {
        int oldValue;
        do {
            oldValue = getNumTrackedObjects();
        } while (!numTrackedObjects.compareAndSet(oldValue, oldValue + num));
    }
}
