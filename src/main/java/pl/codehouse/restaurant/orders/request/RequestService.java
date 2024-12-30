package pl.codehouse.restaurant.orders.request;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service interface for managing restaurant requests.
 * This interface defines operations for updating, retrieving, and monitoring request statuses.
 */
public interface RequestService {
    /**
     * Updates the collected items for a specific request.
     *
     * @param updateDto The DTO containing information about the items to be updated.
     * @return A Mono emitting the updated RequestDto.
     */
    Mono<RequestDto> updateCollectedItems(UpdatePreparedMenuItemsDto updateDto);

    /**
     * Retrieves a request by its ID.
     *
     * @param requestId The ID of the request to retrieve.
     * @return A Mono emitting the RequestDto for the specified ID.
     */
    Mono<RequestDto> findById(int requestId);

    /**
     * Fetches all active requests.
     *
     * @return A Flux emitting RequestDto objects for all active requests.
     */
    Flux<RequestDto> fetchActive();

    /**
     * Listens for updates on request statuses.
     *
     * @return A Flux emitting RequestStatusDto objects whenever a request status changes.
     */
    Flux<RequestStatusDto> listenOnRequestUpdates();
}
