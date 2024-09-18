package pl.codehouse.restaurant.orders;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface OrderRepository extends ReactiveCrudRepository<OrderEntity, Integer> {
}