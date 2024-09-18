package pl.codehouse.restaurant.orders;

import org.springframework.boot.SpringApplication;
import pl.codehouse.restaurant.RestaurantOrdersApplication;

public class TestRestaurantOrdersApplication {

    public static void main(String[] args) {
        SpringApplication.from(RestaurantOrdersApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }

}
