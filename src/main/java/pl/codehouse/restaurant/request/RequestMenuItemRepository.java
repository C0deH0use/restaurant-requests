package pl.codehouse.restaurant.request;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RequestMenuItemRepository extends ReactiveCrudRepository<RequestMenuItemEntity, Integer> {
}