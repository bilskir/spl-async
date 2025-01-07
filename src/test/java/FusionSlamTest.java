import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;

import static org.junit.jupiter.api.Assertions.*;

public class FusionSlamTest {

    private FusionSlam fusionSlam;

    @BeforeEach
    void setUp() {
        fusionSlam = FusionSlam.getInstance();
    }

    @Test
    void testTransformToGlobal_NoRotation() {
        // Pose at (10.0, 20.0) with 0 degrees yaw
        Pose pose = new Pose(10.0f, 20.0f, 0.0f, 1);
        double[] result = fusionSlam.transformToGlobal(5.0, 5.0, pose);

        // Expected global coordinates: (15.0, 25.0)
        assertEquals(15.0, result[0], 0.0001, "Global X coordinate should match expected value.");
        assertEquals(25.0, result[1], 0.0001, "Global Y coordinate should match expected value.");
    }

    @Test
    void testTransformToGlobal_WithRotation() {
        // Pose at (0.0, 0.0) with 90 degrees yaw
        Pose pose = new Pose(0.0f, 0.0f, 90.0f, 1);
        double[] result = fusionSlam.transformToGlobal(5.0, 0.0, pose);

        // Expected global coordinates after 90-degree rotation: (0.0, 5.0)
        assertEquals(0.0, result[0], 0.0001, "Global X coordinate should match expected value.");
        assertEquals(5.0, result[1], 0.0001, "Global Y coordinate should match expected value.");
    }
}
