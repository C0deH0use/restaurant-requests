package pl.codehouse.restaurant.shelf;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
interface ShelfRepository extends ReactiveCrudRepository<ShelfEntity, Integer> {
    Mono<ShelfEntity> findByMenuItemId(Integer menuItemId);
}
