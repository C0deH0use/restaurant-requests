package pl.codehouse.restaurant.request;

/**
 * Data Transfer Object (DTO) for updating the prepared quantity of a menu item in a request.
 * This record encapsulates the information needed to update the preparation status of a specific menu item.
 */
public record UpdatePreparedMenuItemsDto(
        int requestId,
        int menuItemId,
        int preparedQuantity
) {
}
