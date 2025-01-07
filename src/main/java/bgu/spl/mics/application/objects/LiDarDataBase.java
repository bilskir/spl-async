package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {

    private static LiDarDataBase db = null;
    private final CopyOnWriteArrayList<StampedCloudPoints> cloudPoints;
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    // Private constructor to load LiDAR data from file
    private LiDarDataBase(String filePath) {
        this.cloudPoints = new CopyOnWriteArrayList<>();
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<List<StampedCloudPoints>>() {}.getType();
            List<StampedCloudPoints> loadedPoints = gson.fromJson(reader, listType);
            if (loadedPoints != null) {
                cloudPoints.addAll(loadedPoints);
            }
            //System.out.println("LiDar data loaded successfully: " + cloudPoints.size() + " entries.");
        } catch (IOException e) {
            System.err.println("Failed to load LiDar data from: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        lock.readLock().lock();
        try {
            if (db == null) {
                lock.readLock().unlock();
                lock.writeLock().lock();
                try {
                    if (db == null) {
                        db = new LiDarDataBase(filePath);
                    }
                } finally {
                    lock.writeLock().unlock();
                }
                lock.readLock().lock();
            }
            return db;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns the list of stamped cloud points.
     *
     * @return The list of stamped cloud points.
     */
    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }

    /**
     * Removes a CloudPoint from the database.
     *
     * @param object The detected object to remove.
     * @param time   The time stamp of the cloud point to remove.
     */
    public void removeCloudPoint(DetectedObject object, int time) {
        for (StampedCloudPoints cp : cloudPoints) {
            if (cp.getTime() == time && cp.getID().equals(object.getID())) {
                cloudPoints.remove(cp);
                break;
            }
        }
    }

    /**
     * Returns the size of the cloud points list.
     *
     * @return The size of the cloud points list.
     */
    public int getCloudPointsSize() {
        return cloudPoints.size();
    }

    @Override
    public String toString() {
        return "LiDarDataBase{cloudPoints=" + cloudPoints + "}";
    }
}
