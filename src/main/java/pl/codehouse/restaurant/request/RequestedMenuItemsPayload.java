package pl.codehouse.restaurant.request;

/**
 * Represents the payload for a requested menu item in the restaurant system.
 * This record encapsulates the menu ID and the quantity of the item requested.
 */
public record RequestedMenuItemsPayload(
    int menuId,
    int quantity
) {
}
