package pl.codehouse.restaurant.shelf;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import pl.codehouse.restaurant.Command;
import pl.codehouse.restaurant.Context;
import pl.codehouse.restaurant.ExecutionResult;
import pl.codehouse.restaurant.request.PackingActionResult;
import pl.codehouse.restaurant.request.RequestDto;
import pl.codehouse.restaurant.request.RequestMenuItem;
import pl.codehouse.restaurant.request.RequestService;
import pl.codehouse.restaurant.request.UpdatePreparedMenuItemsDto;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Component
class PackingCommand implements Command<Integer, PackingActionResult> {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PackingCommand.class);

    private final ShelfBo shelfBo;
    private final RequestService requestService;

    PackingCommand(ShelfBo shelfBo, RequestService requestService) {
        this.shelfBo = shelfBo;
        this.requestService = requestService;
    }

    @Override
    public Mono<ExecutionResult<PackingActionResult>> execute(Context<Integer> context) {
        Integer requestId = context.request();
        logger.info("Starting collecting requested menu items for request id: {}", requestId);

        return requestService.findById(requestId)
                .flatMapIterable(RequestDto::menuItems)
                .filter(RequestMenuItem::notFinished)
                .doOnNext(signal -> logger.info("Searching for following item on shelf -> {}", signal))
                .flatMap(this::mapShelfTakeResultStatusBasedOnRequestedMenuItem)
                .flatMap(itemStatus -> updatePreparedMenuItems(requestId, itemStatus))
                .then(requestService.findById(requestId).map(this::fromRequest))
                .map(ExecutionResult::success);
    }

    private Mono<RequestDto> updatePreparedMenuItems(int requestId, Tuple2<RequestMenuItem, ShelfTakeResult> itemStatus) {
        RequestMenuItem requestMenuItem = itemStatus.getT1();
        ShelfTakeResult shelfTakeResult = itemStatus.getT2();
        if (shelfTakeResult.itemsTakenFromShelf() == 0) {
            return Mono.empty();
        }
        return requestService.updateCollectedItems(
                new UpdatePreparedMenuItemsDto(requestId, requestMenuItem.menuItemId(), shelfTakeResult.itemsTakenFromShelf()));
    }

    private Mono<Tuple2<RequestMenuItem, ShelfTakeResult>> mapShelfTakeResultStatusBasedOnRequestedMenuItem(RequestMenuItem missingItem) {
        if (missingItem.immediatePreparation()) {
            Tuple2<RequestMenuItem, ShelfTakeResult> immediateItem =
                    Tuples.of(missingItem, new ShelfTakeResult(PackingStatus.READY_TO_COLLECT, missingItem.remainingItems()));
            return Mono.just(immediateItem);
        }
        return Mono.just(missingItem)
                .zipWhen(shelfBo::take);
    }

    private PackingActionResult fromRequest(RequestDto requestDto) {
        return new PackingActionResult(requestDto.requestId(), requestDto.preparedItemsCount(), requestDto.totalItemsCount(), requestDto.status());
    }
}
