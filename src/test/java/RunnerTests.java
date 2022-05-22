import api.Runner;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public final class RunnerTests {

    private static Runner runner;

    @BeforeAll
    public static void setup() {
//        runner = new RunnerImpl();
    }

    @AfterAll
    public static void teardown() {
        runner.cleanup();
    }

    @DisplayName("Test 1 - Scanning has successfully finished?")
    @Order(1)
    @Test
    public void testScanningSuccessful() {
        Assertions.assertTrue(runner.scan());
    }

    @DisplayName("Test 2 - Processing has successfully finished?")
    @Order(2)
    @Test
    public void testProcessingSuccessful() {
        Assertions.assertTrue(runner.process());
    }

    @DisplayName("Test 3 - Random is initialized?")
    @Order(3)
    @Test
    public void testRandomIsInitialized() {
        Assertions.assertNotNull(Injects.random);
    }

    @DisplayName("Test 4 - Random has correct type?")
    @Order(4)
    @Test
    public void testRandomIsCorrectType() {
        Assertions.assertTrue(Random.class.isAssignableFrom(Injects.random.getClass()));
    }

    @DisplayName("Test 5 - Random functions correctly?")
    @Order(5)
    @Test
    public void testRandomFunctionsCorrectly() {
        Assertions.assertEquals(0, Injects.random.nextInt(1));
    }
}
