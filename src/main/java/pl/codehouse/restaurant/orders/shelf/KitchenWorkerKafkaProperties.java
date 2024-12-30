package pl.codehouse.restaurant.orders.shelf;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.support.KafkaHeaders;

/**
 * Configuration properties for Kafka topics related to kitchen worker operations.
 * This record encapsulates the configuration for the Kafka topic that handles
 * kitchen worker events.
 *
 * <p>The properties are typically loaded from the application's configuration
 * file using the prefix "app.kafka.kitchen.topic".</p>
 */
@ConfigurationProperties("app.kafka.kitchen.topic")
public record KitchenWorkerKafkaProperties(
        String topicName,
        int partitions
) {
    /**
     * Generates Kafka headers for the kitchen worker topic.
     * This method creates a Map containing the necessary Kafka headers
     * for publishing messages to the configured topic.
     *
     * @return A Map containing Kafka headers with the topic name.
     *         The key is the Kafka header name (KafkaHeaders.TOPIC),
     *         and the value is the configured topic name.
     */
    public Map<String, Object> kafkaHeaders() {
        return Map.of(
                KafkaHeaders.TOPIC, topicName
        );
    }
}
