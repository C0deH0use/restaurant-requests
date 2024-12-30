package pl.codehouse.restaurant.orders.request;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface MenuItemRepository extends ReactiveCrudRepository<MenuItemEntity, Integer> {
}