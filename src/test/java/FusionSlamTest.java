import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Preconditions: FusionSlam instance must be initialized before tests.
 * Postconditions: Verify transformation calculations are correct.
 * Invariants: Pose objects remain immutable during transformations.
 */
public class FusionSlamTest {
    private FusionSlam fusionSlam;

    @BeforeEach
    void setUp() {
        // Precondition: FusionSlam singleton instance should be available.
        fusionSlam = FusionSlam.getInstance();
    }

    @Test
    void testTransformToGlobal_NoRotation() {
        // Preconditions: Valid Pose object with zero yaw (no rotation).
        Pose pose = new Pose(10.0f, 20.0f, 0.0f, 1);
        
        // Act
        double[] result = fusionSlam.transformToGlobal(5.0, 5.0, pose);

        // Postconditions: Global coordinates should match the expected values.
        assertEquals(15.0, result[0], 0.0001, "Global X coordinate should match expected value.");
        assertEquals(25.0, result[1], 0.0001, "Global Y coordinate should match expected value.");
    }

    @Test
    void testTransformToGlobal_WithRotation() {
        // Preconditions: Valid Pose object with 90 degrees yaw.
        Pose pose = new Pose(0.0f, 0.0f, 90.0f, 1);
        
        // Act
        double[] result = fusionSlam.transformToGlobal(5.0, 0.0, pose);

        // Postconditions: Global coordinates should match the expected values after rotation.
        assertEquals(0.0, result[0], 0.0001, "Global X coordinate should match expected value.");
        assertEquals(5.0, result[1], 0.0001, "Global Y coordinate should match expected value.");
    }
}
