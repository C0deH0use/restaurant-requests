package pl.codehouse.restaurant.request;


public record PackingActionResult(
    int requestId,
    int collectedItems,
    int totalItems,
    RequestStatus status
) {

}
