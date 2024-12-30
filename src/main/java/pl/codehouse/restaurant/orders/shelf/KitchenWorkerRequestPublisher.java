package pl.codehouse.restaurant.orders.shelf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import pl.codehouse.restaurant.orders.request.RequestMenuItem;

@Component
class KitchenWorkerRequestPublisher {
    private static final Logger logger = LoggerFactory.getLogger(KitchenWorkerRequestPublisher.class);

    private final KafkaTemplate<String, KitchenWorkerRequestMessage> kafkaTemplate;
    private final KitchenWorkerKafkaProperties kafkaProperties;

    KitchenWorkerRequestPublisher(KafkaTemplate<String, KitchenWorkerRequestMessage> kafkaTemplate,
                                  KitchenWorkerKafkaProperties kafkaProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }

    void publishRequest(RequestMenuItem menuItem, int quantityToRequest) {
        logger.info("Requesting Kitchen to create {} new {} menu items", quantityToRequest, menuItem.menuItemName());
        KitchenWorkerRequestMessage payload = new KitchenWorkerRequestMessage(menuItem.menuItemId(), quantityToRequest);
        Message<KitchenWorkerRequestMessage> message = new GenericMessage<>(payload, kafkaProperties.kafkaHeaders());
        kafkaTemplate.send(message);
    }
}
