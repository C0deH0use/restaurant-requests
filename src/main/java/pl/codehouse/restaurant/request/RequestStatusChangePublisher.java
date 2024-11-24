package pl.codehouse.restaurant.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import pl.codehouse.restaurant.shelf.PackingStatus;

@Component
class RequestStatusChangePublisher {
    private static final Logger logger = LoggerFactory.getLogger(RequestStatusChangePublisher.class);

    private final KafkaTemplate<String, RequestStatusChangeMessage> kafkaTemplate;
    private final RequestStatusChangeKafkaProperties kafkaProperties;

    RequestStatusChangePublisher(KafkaTemplate<String, RequestStatusChangeMessage> kafkaTemplate,
                                        RequestStatusChangeKafkaProperties kafkaProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }

    void publishChange(int requestId, RequestStatus newStatus, PackingStatus packingStatus) {
        RequestStatusChangeMessage payload = new RequestStatusChangeMessage(requestId, newStatus, packingStatus);
        Message<RequestStatusChangeMessage> message = new GenericMessage<>(payload, kafkaProperties.kafkaHeaders());
        logger.info("Notifying on request status update event: {} for the following request: {}", payload.getClass().getSimpleName(), requestId);
        kafkaTemplate.send(message);
    }
}
