package pl.codehouse.restaurant.request;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
interface RequestRepository extends ReactiveCrudRepository<RequestEntity, Integer> {
    @Query("UPDATE request SET status = :status WHERE id = :requestId")
    Mono<Boolean> updateStatusById(@Param("requestId") int requestId, @Param("status") RequestStatus requestStatus);
}