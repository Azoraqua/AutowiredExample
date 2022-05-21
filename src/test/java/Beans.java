import api.Bean;

import java.security.SecureRandom;
import java.util.Random;

public class Beans {

    @Bean
    public static Random random() {
        return Math.random() >= .5 ? new Random() : new SecureRandom();
    }
}
