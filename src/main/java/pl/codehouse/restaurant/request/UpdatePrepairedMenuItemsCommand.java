package pl.codehouse.restaurant.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.codehouse.restaurant.Command;
import pl.codehouse.restaurant.Context;
import pl.codehouse.restaurant.ExecutionResult;
import pl.codehouse.restaurant.shelf.PackingStatus;
import reactor.core.publisher.Mono;

@Component
class UpdatePrepairedMenuItemsCommand implements Command<UpdatePreparedMenuItemsDto, PackingStatus> {
    private final static Logger logger = LoggerFactory.getLogger(UpdatePrepairedMenuItemsCommand.class);
    private final RequestRepository requestRepository;
    private final RequestMenuItemRepository requestMenuItemRepository;

    UpdatePrepairedMenuItemsCommand(RequestRepository requestRepository, RequestMenuItemRepository requestMenuItemRepository, MenuItemRepository menuItemRepository) {
        this.requestRepository = requestRepository;
        this.requestMenuItemRepository = requestMenuItemRepository;
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
                    if (!allItemsCollected) {
                        return requestRepository.updateStatusById(requestId, RequestStatus.IN_PROGRESS)
                                .then().thenReturn(PackingStatus.IN_PROGRESS);
                    }

                    logger.info("All menu items are prepared, request {} ready to be collected", requestId);
                    return requestRepository.updateStatusById(requestId, RequestStatus.READY_TO_COLLECT)
                            .thenReturn(PackingStatus.READY_TO_COLLECT);
                })
                .map(ExecutionResult::success);
    }
}
