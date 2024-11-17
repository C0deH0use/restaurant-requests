package pl.codehouse.restaurant.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.codehouse.restaurant.Command;
import pl.codehouse.restaurant.Context;
import pl.codehouse.restaurant.ExecutionResult;
import pl.codehouse.restaurant.shelf.PackingStatus;
import reactor.core.publisher.Mono;

/**
 * Command for updating the prepared count of menu items in a request.
 * This component handles the business logic for updating the preparation status
 * of menu items and notifying about status changes.
 */
@Component
class UpdatePrepairedMenuItemsCommand implements Command<UpdatePreparedMenuItemsDto, PackingStatus> {
    private static final Logger logger = LoggerFactory.getLogger(UpdatePrepairedMenuItemsCommand.class);
    private final RequestRepository requestRepository;
    private final RequestMenuItemRepository requestMenuItemRepository;
    private final KafkaTemplate<String, ShelfEventDto> kafkaTemplate;
    private final RequestStatusChangeKafkaProperties requestStatusChangeKafkaProperties;

    UpdatePrepairedMenuItemsCommand(RequestRepository requestRepository,
                                    RequestMenuItemRepository requestMenuItemRepository,
                                    KafkaTemplate<String, ShelfEventDto> kafkaTemplate,
                                    RequestStatusChangeKafkaProperties requestStatusChangeKafkaProperties) {
        this.requestRepository = requestRepository;
        this.requestMenuItemRepository = requestMenuItemRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.requestStatusChangeKafkaProperties = requestStatusChangeKafkaProperties;
    }

    @Override
    @Transactional
    public Mono<ExecutionResult<PackingStatus>> execute(Context<UpdatePreparedMenuItemsDto> context) {
        UpdatePreparedMenuItemsDto updateDto = context.request();
        int requestId = updateDto.requestId();
        logger.info("Updating request menu item prepared count: {}", updateDto);
        return requestMenuItemRepository.findByRequestIdAndMenuItemId(requestId, updateDto.menuItemId())
                .flatMap(requestMenuItem -> requestMenuItemRepository.save(requestMenuItem.withUpdatedPreparedCnt(updateDto.preparedQuantity())))
                .flatMap(requestMenuItem -> requestMenuItemRepository.findByRequestId(requestId).collectList())
                .flatMap(requestMenuItemEntities -> {
                    logger.info("Checking Request Menu Item status -> {}", requestMenuItemEntities);
                    boolean allItemsCollected = requestMenuItemEntities.stream().allMatch(RequestMenuItemEntity::isFinished);
                    RequestStatus newStatus = allItemsCollected ? RequestStatus.READY_TO_COLLECT : RequestStatus.IN_PROGRESS;
                    PackingStatus packingStatus = allItemsCollected ? PackingStatus.READY_TO_COLLECT : PackingStatus.IN_PROGRESS;

                    return requestRepository.updateStatusById(requestId, newStatus)
                            .then(notifyStatusChange(requestId, newStatus, packingStatus))
                            .thenReturn(packingStatus);
                })
                .map(ExecutionResult::success);
    }

    private Mono<Void> notifyStatusChange(int requestId, RequestStatus newStatus, PackingStatus packingStatus) {
        Message<RequestStatusChangeMessage> requestStatusMessage = new GenericMessage<>(
                new RequestStatusChangeMessage(requestId, newStatus, packingStatus),
                requestStatusChangeKafkaProperties.kafkaHeaders()
        );
        logger.info("Notifying on status update event: {} for the following request: {}", requestStatusMessage.getPayload().getClass().getSimpleName(),
                requestId);
        kafkaTemplate.send(requestStatusMessage);
        return Mono.empty();
    }
}
