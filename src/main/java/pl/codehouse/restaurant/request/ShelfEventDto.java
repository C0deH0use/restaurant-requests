package pl.codehouse.restaurant.request;

public record ShelfEventDto(
        EventType eventType,
        Integer requestId,
        int menuItemId,
        int quantity
) {

    private static final int NEW_VALUE = -1;

    public static ShelfEventDto newRequestEvent(int requestId) {
        return new ShelfEventDto(EventType.NEW_REQUEST, requestId, NEW_VALUE, NEW_VALUE);
    }
}
