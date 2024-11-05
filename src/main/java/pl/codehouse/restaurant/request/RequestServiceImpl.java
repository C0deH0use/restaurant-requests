package pl.codehouse.restaurant.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.codehouse.restaurant.Context;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {

    private static final Logger logger = LoggerFactory.getLogger(RequestServiceImpl.class);

    private static final List<RequestStatus> ACTIVE_REQUEST_STATUSES = List.of(RequestStatus.NEW, RequestStatus.IN_PROGRESS, RequestStatus.READY_TO_COLLECT);

    private final RequestRepository requestRepository;
    private final RequestMenuItemRepository requestMenuItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UpdatePrepairedMenuItemsCommand updatePrepairedMenuItemsCommand;

    public RequestServiceImpl(RequestRepository requestRepository,
                              RequestMenuItemRepository requestMenuItemRepository,
                              MenuItemRepository menuItemRepository,
                              UpdatePrepairedMenuItemsCommand updatePrepairedMenuItemsCommand) {
        this.requestRepository = requestRepository;
        this.requestMenuItemRepository = requestMenuItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.updatePrepairedMenuItemsCommand = updatePrepairedMenuItemsCommand;
    }

    @Override
    public Mono<RequestDto> updateCollectedItems(UpdatePreparedMenuItemsDto updateDto) {
        return updatePrepairedMenuItemsCommand.execute(new Context<>(updateDto))
                .then(findById(updateDto.requestId()));
    }

    @Override
    public Mono<RequestDto> findById(int requestId) {
        Mono<RequestEntity> entityMono = requestRepository.findById(requestId);
        Mono<Tuple2<List<RequestMenuItemEntity>, List<MenuItemEntity>>> tuple2Mono = requestMenuItemRepository.findByRequestId(requestId)
                .collectList()
                .flatMap(getListMonoFunction());
        return Mono.zip(entityMono, tuple2Mono)
                .map(tuple2 -> RequestDto.from(tuple2.getT1(), tuple2.getT2().getT1(), tuple2.getT2().getT2()));
    }

    @Override
    public Flux<RequestDto> fetchActive() {
        return requestRepository.findByStatus(ACTIVE_REQUEST_STATUSES)
                .doOnComplete(() -> logger.info(">>.findByStatus({}) completed", ACTIVE_REQUEST_STATUSES))
                .doOnNext(r -> logger.info("Fetching components of RequestDTO for {}, status: {}", r.id(), r.status()))
                .flatMap(request -> requestMenuItemRepository.findByRequestId(request.id())
                        .collectList()
                        .flatMap(getListMonoFunction())
                        .map(tuple -> RequestDto.from(request, tuple.getT1(), tuple.getT2())));
    }

    private Function<List<RequestMenuItemEntity>, Mono<? extends Tuple2<List<RequestMenuItemEntity>, List<MenuItemEntity>>>> getListMonoFunction() {
        return entities -> {
            Set<Integer> menuItemIds = entities.stream()
                    .map(RequestMenuItemEntity::menuItemId)
                    .collect(Collectors.toSet());
            return Mono.zip(Mono.just(entities), menuItemRepository.findAllById(menuItemIds).collectList());
        };
    }
}
