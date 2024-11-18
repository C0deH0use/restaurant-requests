package pl.codehouse.restaurant.request;

import java.util.List;

/**
 * Represents the payload of a request in the restaurant system.
 * This record encapsulates the list of requested menu items and the customer ID associated with the request.
 */
record RequestPayload(List<RequestedMenuItemsPayload> menuItems, int customerId) {
    /**
     * Constructs a new RequestPayload with the given menu items and customer ID.
     * This constructor ensures that the list of menu items is immutable.
     *
     * @param menuItems The list of requested menu items.
     * @param customerId The ID of the customer making the request.
     */
    public RequestPayload {
        menuItems = List.copyOf(menuItems);
    }
}
