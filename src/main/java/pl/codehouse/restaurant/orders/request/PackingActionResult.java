package pl.codehouse.restaurant.orders.request;

/**
 * Represents the result of a packing action for a restaurant request.
 * This record encapsulates information about the packing progress and status of a request.
 */
public record PackingActionResult(
    int requestId,
    int collectedItems,
    int totalItems,
    RequestStatus status
) {

}
