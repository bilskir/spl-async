package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

public class StatisticalFolder {
    private static final StatisticalFolder instance = new StatisticalFolder();

    private final AtomicInteger systemRuntime = new AtomicInteger();
    private final AtomicInteger numDetectedObjects = new AtomicInteger();
    private final AtomicInteger numTrackedObjects = new AtomicInteger();
    private final AtomicInteger numLandmarks = new AtomicInteger();

    private StatisticalFolder() {
    }

    public static StatisticalFolder getInstance() {
        return instance;
    }

    public synchronized void addSystemRuntime(int runtime) {
        systemRuntime.addAndGet(runtime);
    }

    public void addNumDetectedObjects(int num) {
        synchronized (this) {
            int oldValue = numDetectedObjects.get();
            numDetectedObjects.set(oldValue + num);
            System.out.println("numDetectedObjects has been called!");
        }
    }

    public synchronized void addNumTrackedObjects(int count) {
        numTrackedObjects.addAndGet(count);
    }

    public synchronized void addNumLandmarks(int count) {
        numLandmarks.addAndGet(count);
    }

    public int getSystemRuntime() {
        return systemRuntime.get();
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    public int getNumLandmarks() {
        return numLandmarks.get();
    }
}