package pl.codehouse.restaurant.orders.request;

import java.util.List;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
interface RequestRepository extends ReactiveCrudRepository<RequestEntity, Integer> {

    @Modifying
    @Query("UPDATE request SET status = :status WHERE id = :requestId")
    Mono<Boolean> updateStatusById(@Param("requestId") int requestId, @Param("status") RequestStatus requestStatus);

    @Query("SELECT * FROM request WHERE status IN (:statuses)")
    Flux<RequestEntity> findByStatus(@Param("statuses") List<RequestStatus> requestStatus);
}
