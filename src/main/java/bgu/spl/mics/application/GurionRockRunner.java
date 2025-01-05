package bgu.spl.mics.application;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.jsonClasses.Configuration;
import bgu.spl.mics.application.jsonClasses.Configuration.CameraConfiguration;
import bgu.spl.mics.application.jsonClasses.Configuration.LidarConfiguration;
import bgu.spl.mics.application.messages.StartSimulationEvent;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.services.TimeService;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        System.out.println("Hello GurionRocK!");
        if (args.length < 1) {
            System.out.println("Please provide the path to the configuration file as the first argument.");
            return;
        }

        String configurationPath = args[0];
        Gson gson = new Gson(); 
        try {
            Configuration configuration = gson.fromJson(new FileReader(configurationPath), Configuration.class);
            System.out.println("Configuration parsed successfully.");

            // Use the configuration object to initialize system components
            // Example: System.out.println(configuration.getCameras().getCameraDatasPath());
            String cameraPath = configuration.getCameras().getCameraDatasPath();
            String LidarPath = configuration.getLidars().getLidarsDataPath();
            String posePath = configuration.getPoseJsonFile();
            int tickTime = configuration.getTickTime();
            int duration = configuration.getDuration(); 
            int sensorsCounter = 0;
            
            // Initialize services
            TimeService timeService = new TimeService(tickTime, duration);
             
            // Initialize GPSIMU and PoseService
            GPSIMU gpsimu = new GPSIMU(posePath);
            PoseService poseService = new PoseService(gpsimu);
            sensorsCounter++;
            // System.out.println(poseService);

            // Initialize camera
            List<CameraService> cameraServices = new ArrayList<>();
            for(CameraConfiguration config : configuration.getCameras().getCamerasConfigurations()){
                Camera camera = new Camera(config.getId(), config.getFrequency(),cameraPath,config.getCameraKey());
                CameraService cameraService = new CameraService(camera);
                cameraServices.add(cameraService);
                sensorsCounter++;
                //System.out.println(cameraService.toString());
                
            }

            // Intialize lidars 
            List<LiDarService> lidarServiceList = new ArrayList<>();
            LiDarDataBase db = LiDarDataBase.getInstance(LidarPath);

            for (LidarConfiguration config : configuration.getLidars().getLidarConfigurations()){
                LiDarWorkerTracker lidar = new LiDarWorkerTracker(config.getId(), config.getFrequency(), db);
                LiDarService liDarService = new LiDarService(lidar);
                lidarServiceList.add(liDarService);
                sensorsCounter++;
                //System.out.println(liDarService.toString());;
            }
            //System.out.println(db);

            // Initliaze FusionSlam
            FusionSlamService fusionSlamService = new FusionSlamService(sensorsCounter);    
            //System.out.println(fusionSlamService.toString());
            
            CountDownLatch latch = new CountDownLatch(sensorsCounter + 2);

            MessageBusImpl.getInstance().setLatch(latch);
            
            ExecutorService executor = Executors.newFixedThreadPool(sensorsCounter + 2);
           
            for(LiDarService ls : lidarServiceList){
                executor.execute(ls);
            }
            for(CameraService cs : cameraServices){
                executor.execute(cs);
            }
            
            executor.execute(poseService);
            executor.execute(fusionSlamService);
            executor.execute(timeService);

            latch.await();



            System.out.println("bla bla");
            // Send the StartSimulationEvent
            MessageBusImpl.getInstance().sendEvent(new StartSimulationEvent());

            // Await termination of all services
            executor.shutdown();
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println("Executor did not terminate in time. Forcing shutdown...");
                executor.shutdownNow();
            }

            System.out.println("Simulation finished successfully.");

        } catch (FileNotFoundException | InterruptedException e) {
            System.err.println("Configuration file not found: " + configurationPath);
            e.printStackTrace();
        } catch (JsonSyntaxException | JsonIOException e) {
            System.err.println("Failed to parse configuration file.");
            e.printStackTrace();
        }   
        // TODO: Initialize system components and services.

        
        // TODO: Start the simulation.

    }
}
