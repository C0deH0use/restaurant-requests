package pl.codehouse.restaurant.request;

public record UpdatePreparedMenuItemsDto(
        int requestId,
        int menuItemId,
        int preparedQuantity
) {
}
