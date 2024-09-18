package pl.codehouse.restaurant.orders;

import java.util.List;

record OrderRequest(List<Integer> menuItemId, int customerId) {
    public OrderRequest {
        menuItemId = List.copyOf(menuItemId);
    }
}