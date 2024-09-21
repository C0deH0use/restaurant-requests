package pl.codehouse.restaurant.request;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RequestRepository extends ReactiveCrudRepository<RequestEntity, Integer> {
}