package pl.codehouse.restaurant.request;

import org.springframework.boot.SpringApplication;
import pl.codehouse.restaurant.RestaurantOrdersApplication;

public class TestRestaurantOrdersApplication {

    public static void main(String[] args) {
        SpringApplication.from(RestaurantOrdersApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }

}
