package bgu.spl.mics.application.objects;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */

    private static LiDarDataBase db = null;
    private List<StampedCloudPoints> cloudPoints;
    private static ReadWriteLock lock = new ReentrantReadWriteLock();;

    // Use Gson to parse the filePath and add all the cloud points from the file to the list of cloudPoints
    private LiDarDataBase(String filePath){
    Gson gson = new Gson();
    try (FileReader reader = new FileReader(filePath)){
            Type stampedCloudPointsType = new TypeToken<LinkedList<StampedCloudPoints>>(){}.getType();
            cloudPoints = gson.fromJson(reader, stampedCloudPointsType);
        }
        catch (IOException e){
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
        if (db == null) {
            lock.readLock().unlock();
            lock.writeLock().lock(); 
            try {
                if (db == null) { // Double-check locking
                    db = new LiDarDataBase(filePath);
                }
            } finally {
                lock.writeLock().unlock(); 
            }
            lock.readLock().lock();
        }

        try {
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
        lock.readLock().lock(); 
        try{
            return cloudPoints; 
        } finally {
            lock.readLock().unlock();
        }
    }
    
     /**
     *  Removes a CloudPoint from the database.
     *
     * @param object The detected object to remove.
     * @param time The time stamp of the cloud point to remove.
     */
    public void removeCloudPoint(DetectedObject object, int time) {
        lock.writeLock().lock(); 
        try {
            for (StampedCloudPoints cp : cloudPoints) {
                if (cp.getTime() == time && cp.getID().equals(object.getID())) {
                    cloudPoints.remove(cp);
                    break; 
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


     /**
     * Returns the size of the cloud points list.
     *
     * @return The size of the cloud points list.
     */
    public int getCloudPointsSize() {
        lock.readLock().lock();
        try {
            return cloudPoints.size();
        } finally {
            lock.readLock().unlock();
        }
    }
}
