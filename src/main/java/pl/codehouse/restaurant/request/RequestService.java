package pl.codehouse.restaurant.request;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RequestService {
    Mono<RequestDto> updateCollectedItems(UpdatePreparedMenuItemsDto updateDto);
    Mono<RequestDto> findById(int requestId);
    Flux<RequestDto> fetchActive();
}
