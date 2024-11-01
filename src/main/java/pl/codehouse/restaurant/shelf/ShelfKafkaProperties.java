package pl.codehouse.restaurant.shelf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.Map;

@ConfigurationProperties("app.kafka.shelf.topic")
public record ShelfKafkaProperties(
        String topicName,
        int partitions
) {
    public Map<String, Object> kafkaHeaders() {
        return Map.of(
                KafkaHeaders.TOPIC, topicName
        );
    }
}
