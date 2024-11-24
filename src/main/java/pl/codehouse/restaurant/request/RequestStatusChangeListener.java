package pl.codehouse.restaurant.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
class RequestStatusChangeListener {

    private final Sinks.Many<RequestStatusChangeMessage> notificationSink;

    private static final Logger logger = LoggerFactory.getLogger(RequestStatusChangeListener.class);

    RequestStatusChangeListener() {
        this.notificationSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @KafkaListener(
            topics = "${app.kafka.request-status.topic.topic-name}",
            groupId = "request-status-change-group")
    void listen(RequestStatusChangeMessage message) {
        logger.info("Received request status change: {}", message);
        notificationSink.tryEmitNext(message);
    }

    Flux<RequestStatusChangeMessage> getRequestStatusChanges() {
        return notificationSink.asFlux();
    }
}
