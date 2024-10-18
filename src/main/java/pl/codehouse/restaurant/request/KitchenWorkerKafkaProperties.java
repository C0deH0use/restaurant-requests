package pl.codehouse.restaurant.request;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.Map;

@ConfigurationProperties("app.kafka.kitchen.topic")
public record KitchenWorkerKafkaProperties(
        String topicName,
        int partitions
) {
    public Map<String, Object> kafkaHeaders() {
        return Map.of(
                KafkaHeaders.TOPIC, topicName
        );
    }
}
