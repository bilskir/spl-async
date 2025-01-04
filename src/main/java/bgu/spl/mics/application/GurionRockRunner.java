package bgu.spl.mics.application;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import bgu.spl.mics.application.jsonClasses.Configuration;
import bgu.spl.mics.application.jsonClasses.Configuration.CameraConfiguration;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.services.CameraService;
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
            
            // Initialize services
            TimeService timeService = new TimeService(tickTime, duration);


            // Initialize GPSIMU and PoseService
            GPSIMU gpsimu = new GPSIMU(posePath);
            PoseService poseService = new PoseService(gpsimu);
            System.out.println(poseService);

            // Initialize camera
            List<CameraService> cameraServices = new ArrayList<>();
            for(CameraConfiguration config : configuration.getCameras().getCamerasConfigurations()){
                Camera camera = new Camera(config.getId(), config.getFrequency(),cameraPath,config.getCameraKey());
                CameraService cameraService = new CameraService(camera);
                cameraServices.add(cameraService);
                System.out.println(cameraService.toString());
                
            }

            // Intialize lidar

            // // Initialize camera and lidar services based on configuration
            // configuration.getCameras().getCamerasConfigurations().forEach(cameraConfig -> {
            //     new Thread(new CameraService(cameraConfig.getId(), cameraPath, cameraConfig.getCameraKey())).start();
            // });

            // configuration.getLidars().getLidarConfigurations().forEach(lidarConfig -> {
            //     new Thread(new LidarWorkerService(lidarConfig.getId(), lidarPath)).start();
            // });

            // // Start time service and pose service in separate threads
            // ExecutorService executor = Executors.newFixedThreadPool(2);
            // executor.execute(new Thread(timeService));
            // executor.execute(new Thread(poseService));

            // // Shutdown executor after simulation
            // executor.shutdown();


        } catch (FileNotFoundException e) {
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
