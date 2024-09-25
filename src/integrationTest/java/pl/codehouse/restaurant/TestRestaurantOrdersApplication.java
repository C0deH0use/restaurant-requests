package pl.codehouse.restaurant;

import org.springframework.boot.SpringApplication;

public class TestRestaurantOrdersApplication {

    public static void main(String[] args) {
        SpringApplication.from(RestaurantOrdersApplication::main)
                .with(pl.codehouse.restaurant.TestcontainersConfiguration.class)
                .run(args);
    }

}
