package pl.codehouse.restaurant.request;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.support.KafkaHeaders;

/**
 * Configuration properties for Kafka topics related to request status changes.
 * This record is used to encapsulate the configuration for the Kafka topic
 * that handles request status change events.
 *
 * <p>The properties are typically loaded from the application's configuration
 * file using the prefix "app.kafka.request-status.topic".</p>
 */
@ConfigurationProperties("app.kafka.request-status.topic")
public record RequestStatusChangeKafkaProperties(
        String topicName,
        int partitions
) {
    /**
     * Creates a new RequestStatusChangeKafkaProperties instance.
     *
     * @param topicName  The name of the Kafka topic for request status changes.
     *                   This should be a unique identifier for the topic within the Kafka cluster.
     * @param partitions The number of partitions for the Kafka topic.
     *                   This determines how the topic data is distributed across the Kafka cluster.
     */
    public RequestStatusChangeKafkaProperties {
        if (topicName == null || topicName.isBlank()) {
            throw new IllegalArgumentException("Topic name cannot be null or blank");
        }
        if (partitions <= 0) {
            throw new IllegalArgumentException("Number of partitions must be greater than zero");
        }
    }

    /**
     * Generates Kafka headers for the request status change topic.
     * This method creates a Map containing the necessary Kafka headers
     * for publishing messages to the configured topic.
     *
     * @return A Map containing Kafka headers with the topic name.
     *          The key is the Kafka header name (KafkaHeaders.TOPIC),
     *          and the value is the configured topic name.
     */
    public Map<String, Object> kafkaHeaders() {
        return Map.of(
                KafkaHeaders.TOPIC, topicName
        );
    }
}
