package bgu.spl.mics.application.jsonClasses;

import java.util.List;

import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.StatisticalFolder;

public class Stats {
    int systemRuntime;
    int numDetectedObjects;
    int numTrackedObjects;
    int numLandmarks;
    List<LandMark> lanmdarks;

    public Stats(StatisticalFolder sFolder, List<LandMark> lanmdarks) {
        this.systemRuntime = sFolder.getSystemRuntime();
        this.numDetectedObjects = sFolder.getNumDetectedObjects();
        this.numTrackedObjects = sFolder.getNumTrackedObjects();
        this.numLandmarks = sFolder.getNumLandmarks();
        this.lanmdarks = lanmdarks;
    }
}
