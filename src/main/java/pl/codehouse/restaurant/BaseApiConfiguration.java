package pl.codehouse.restaurant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BaseApiConfiguration {
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
