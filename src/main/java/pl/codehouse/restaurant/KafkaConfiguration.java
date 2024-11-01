package pl.codehouse.restaurant;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import pl.codehouse.restaurant.request.KitchenWorkerKafkaProperties;
import pl.codehouse.restaurant.shelf.ShelfKafkaProperties;

@Configuration
class KafkaConfiguration {
    private final KitchenWorkerKafkaProperties kitchenWorkerKafkaProperties;
    private final ShelfKafkaProperties shelfKafkaProperties;

    KafkaConfiguration(
            KitchenWorkerKafkaProperties kitchenWorkerKafkaProperties,
            ShelfKafkaProperties shelfKafkaProperties
    ) {
        this.kitchenWorkerKafkaProperties = kitchenWorkerKafkaProperties;
        this.shelfKafkaProperties = shelfKafkaProperties;
    }

    @Bean
    NewTopic kitchenWorkersTopic() {
        return TopicBuilder.name(kitchenWorkerKafkaProperties.topicName())
                .partitions(kitchenWorkerKafkaProperties.partitions())
                .build();
    }

    @Bean
    NewTopic shelfTopic() {
        return TopicBuilder.name(shelfKafkaProperties.topicName())
                .partitions(kitchenWorkerKafkaProperties.partitions())
                .build();
    }
}
