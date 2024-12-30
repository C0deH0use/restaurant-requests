package pl.codehouse.restaurant.orders;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class RestaurantRequestsApplicationTests {

    @Test
    void contextLoads() {
    }

}
