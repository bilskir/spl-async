package bgu.spl.mics.application;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import bgu.spl.mics.application.jsonClasses.Configuration;

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
        // TODO: Parse configuration file.  
        String configurationPath = args[0];
        Gson gson = new Gson(); 
        try {
            Configuration configuration = gson.fromJson(new FileReader(configurationPath), Configuration.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // list of threads here
        
        // TODO: Initialize system components and services.

        // TODO: Start the simulation.

    }
}
