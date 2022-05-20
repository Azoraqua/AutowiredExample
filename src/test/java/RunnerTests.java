import api.Runner;
import impl.RunnerImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

@ExtendWith(MockitoExtension.class)
public final class RunnerTests {

    private static Runner runner;
    private static Random random;

    @BeforeAll
    public static void setup() {
        runner = new RunnerImpl();
    }

    @Test
    public void testScanningSuccessful() {
        Assertions.assertTrue(runner.scan());
    }

    @Test
    public void testProcessingSuccessful() {
        Assertions.assertTrue(runner.process());
    }

    @Test
    public void testRandomIsInitialized() {
        Assertions.assertNotNull(random);

        Random r = Mockito.mock(random.getClass(), Mockito.withSettings().withoutAnnotations());
        Mockito.when(r.nextInt()).thenReturn(1);

        Assertions.assertEquals("The number is " + r.nextInt(), "The number is 1");
    }
}
