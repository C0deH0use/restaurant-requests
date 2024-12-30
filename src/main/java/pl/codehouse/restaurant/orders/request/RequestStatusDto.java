package pl.codehouse.restaurant.orders.request;

import pl.codehouse.restaurant.orders.shelf.PackingStatus;

/**
 * Represents the status of a request in the restaurant system.
 * This data transfer object encapsulates information about the request's packing status and item counts.
 */
public record RequestStatusDto(
        int requestId,
        PackingStatus status,
        int preparedItems,
        int totalItems
) {
}
