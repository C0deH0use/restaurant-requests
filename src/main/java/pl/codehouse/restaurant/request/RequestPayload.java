package pl.codehouse.restaurant.request;

import java.util.List;

record RequestPayload(List<RequestedMenuItemsPayload> menuItems, int customerId) {
    public RequestPayload {
        menuItems = List.copyOf(menuItems);
    }
}