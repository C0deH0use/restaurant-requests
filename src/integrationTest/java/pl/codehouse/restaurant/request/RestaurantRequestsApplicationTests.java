package pl.codehouse.restaurant.request;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import pl.codehouse.restaurant.TestcontainersConfiguration;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class RestaurantRequestsApplicationTests {

    @Test
    void contextLoads() {
    }

}
