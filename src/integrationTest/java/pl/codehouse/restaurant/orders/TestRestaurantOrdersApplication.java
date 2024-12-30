package pl.codehouse.restaurant.orders;

import org.springframework.boot.SpringApplication;

public class TestRestaurantOrdersApplication {

    public static void main(String[] args) {
        SpringApplication.from(RestaurantOrdersApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }

}
