import api.Bean;

import java.util.Random;

public class Beans {

    @Bean
    public static Random random() {
        return new Random();
    }
}
