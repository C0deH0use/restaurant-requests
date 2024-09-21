package pl.codehouse.restaurant.request;

import java.util.List;

record RequestPayload(List<Integer> menuItems, int customerId) {
    public RequestPayload {
        menuItems = List.copyOf(menuItems);
    }
}