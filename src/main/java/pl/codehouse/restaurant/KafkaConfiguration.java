package pl.codehouse.restaurant;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import pl.codehouse.restaurant.shelf.KitchenWorkerKafkaProperties;
import pl.codehouse.restaurant.request.RequestStatusChangeKafkaProperties;
import pl.codehouse.restaurant.shelf.ShelfKafkaProperties;

/**
 * Configuration class for Kafka-related settings.
 */
@Configuration
class KafkaConfiguration {
    private final ShelfKafkaProperties shelfKafkaProperties;
    private final KitchenWorkerKafkaProperties kitchenWorkerKafkaProperties;
    private final RequestStatusChangeKafkaProperties requestStatusChangeKafkaProperties;

    /**
     * Constructs a KafkaConfiguration with the necessary Kafka properties.
     *
     * @param kitchenWorkerKafkaProperties Properties for the kitchen worker Kafka topic.
     * @param shelfKafkaProperties Properties for the shelf Kafka topic.
     * @param requestStatusChangeKafkaProperties Properties for the request status change Kafka topic.
     */
    KafkaConfiguration(
            ShelfKafkaProperties shelfKafkaProperties,
            KitchenWorkerKafkaProperties kitchenWorkerKafkaProperties,
            RequestStatusChangeKafkaProperties requestStatusChangeKafkaProperties
    ) {
        this.shelfKafkaProperties = shelfKafkaProperties;
        this.kitchenWorkerKafkaProperties = kitchenWorkerKafkaProperties;
        this.requestStatusChangeKafkaProperties = requestStatusChangeKafkaProperties;
    }

    /**
     * Creates a new Kafka topic for kitchen workers.
     *
     * @return A NewTopic instance for the kitchen workers.
     */
    @Bean
    NewTopic kitchenWorkersTopic() {
        return TopicBuilder.name(kitchenWorkerKafkaProperties.topicName())
                .partitions(kitchenWorkerKafkaProperties.partitions())
                .build();
    }

    /**
     * Creates a new Kafka topic for shelf events.
     *
     * @return A NewTopic instance for the shelf.
     */
    @Bean
    NewTopic shelfTopic() {
        return TopicBuilder.name(shelfKafkaProperties.topicName())
                .partitions(shelfKafkaProperties.partitions())
                .build();
    }

    /**
     * Creates a new Kafka topic for request status changes.
     *
     * @return A NewTopic instance for request status changes.
     */
    @Bean
    NewTopic requestStatusChangeTopic() {
        return TopicBuilder.name(requestStatusChangeKafkaProperties.topicName())
                .partitions(requestStatusChangeKafkaProperties.partitions())
                .build();
    }
}
