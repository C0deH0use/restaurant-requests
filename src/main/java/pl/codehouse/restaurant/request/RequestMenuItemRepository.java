package pl.codehouse.restaurant.request;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
interface RequestMenuItemRepository extends ReactiveCrudRepository<RequestMenuItemEntity, Integer> {
    Mono<RequestMenuItemEntity> findByRequestIdAndMenuItemId(int requestId, int menuItemId);
    Flux<RequestMenuItemEntity> findByRequestId(int requestId);
}
