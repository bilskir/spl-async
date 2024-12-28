package bgu.spl.mics.application.objects;
import java.util.List;
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

    // Use Gson to parse the filePath and add all the cloud points from the file to the list of cloudPoints
    private LiDarDataBase(String filePath){
    Gson gson = new Gson();
    try (FileReader reader = new FileReader(filePath)){
            Type stampedCloudPointsType = new TypeToken<List<StampedCloudPoints>>(){}.getType();
            cloudPoints = gson.fromJson(reader, stampedCloudPointsType);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public static LiDarDataBase getInstance(String filePath) {
        if(db == null){
            db = new LiDarDataBase(filePath);
        }
        
        return db;
    }
    
    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }
    
}
