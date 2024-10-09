package pl.codehouse.restaurant.request;

import org.springframework.stereotype.Service;
import pl.codehouse.restaurant.Context;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {

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
                .flatMap(requestMenuItemEntities -> {
                    Set<Integer> menuItemIds = requestMenuItemEntities.stream()
                            .map(RequestMenuItemEntity::menuItemId)
                            .collect(Collectors.toSet());
                    return Mono.zip(Mono.just(requestMenuItemEntities), menuItemRepository.findAllById(menuItemIds).collectList());
                });
        return Mono.zip(entityMono, tuple2Mono)
                .map(tuple2 -> RequestDto.from(tuple2.getT1(), tuple2.getT2().getT1(), tuple2.getT2().getT2()));
    }
}
