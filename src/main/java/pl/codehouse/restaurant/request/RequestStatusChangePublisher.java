package pl.codehouse.restaurant.request;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import pl.codehouse.restaurant.shelf.PackingStatus;

@Component
class RequestStatusChangePublisher {

    private final KafkaTemplate<String, RequestStatusChangeMessage> kafkaTemplate;
    private final RequestStatusChangeKafkaProperties kafkaProperties;

    RequestStatusChangePublisher(KafkaTemplate<String, RequestStatusChangeMessage> kafkaTemplate,
                                        RequestStatusChangeKafkaProperties kafkaProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }

    void publishChange(int requestId, RequestStatus newStatus) {
        RequestStatusChangeMessage payload = new RequestStatusChangeMessage(requestId, newStatus, PackingStatus.IN_PROGRESS);
        Message<RequestStatusChangeMessage> message = new GenericMessage<>(payload, kafkaProperties.kafkaHeaders());
        kafkaTemplate.send(message);
    }
}
