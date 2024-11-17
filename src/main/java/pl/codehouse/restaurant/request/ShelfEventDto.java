package pl.codehouse.restaurant.request;

/**
 * Represents a data transfer object for shelf events in the restaurant system.
 * This record encapsulates information about events related to shelf operations,
 * such as new requests or changes in item quantities.
 */
public record ShelfEventDto(
        EventType eventType,
        Integer requestId,
        int menuItemId,
        int quantity
) {

    /**
     * Constant value used to represent a new or unspecified value for menuItemId and quantity.
     */
    private static final int NEW_VALUE = -1;

    /**
     * Creates a new ShelfEventDto instance representing a new request event.
     *
     * @param requestId The ID of the new request.
     * @return A ShelfEventDto instance with EventType.NEW_REQUEST and default values for menuItemId and quantity.
     */
    public static ShelfEventDto newRequestEvent(int requestId) {
        return new ShelfEventDto(EventType.NEW_REQUEST, requestId, NEW_VALUE, NEW_VALUE);
    }
}
