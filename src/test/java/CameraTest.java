

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class CameraTest {
    private Camera camera;

    @BeforeEach
    void setUp() {
        // Initialize a Camera object with sample data
        camera = new Camera(1, 2, "/workspaces/spl-ass2/example_input_2/camera_data.json", "camera1");
    }

    @Test
    void testAddEvent_ValidEvent() {
        // Arrange
        StampedDetectedObjects stampedDetectedObjects = new StampedDetectedObjects(1, Arrays.asList(new DetectedObject("Wall_1", "Wall")));
        DetectObjectsEvent event = new DetectObjectsEvent(stampedDetectedObjects, 1);

        // Act
        boolean result = camera.addEvent(event, 3);
        assertEquals(camera.getEvent(3 + camera.getFrequency()), event, "The event should be added to the camera.");

        // Assert
        assertTrue(result, "The event should be successfully added when within the camera duration.");
    }

    @Test
    void testAddEvent_InvalidEvent() {
        // Arrange
        StampedDetectedObjects stampedDetectedObjects = new StampedDetectedObjects(1, Arrays.asList(new DetectedObject("Wall_1", "Wall")));
        DetectObjectsEvent event = new DetectObjectsEvent(stampedDetectedObjects, 1);

        // Act
        boolean result = camera.addEvent(event, 100); // Tick is beyond camera duration

        // Assert
        assertFalse(result, "The event should not be added if the tick exceeds the camera duration.");
    }
}
