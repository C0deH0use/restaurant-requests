package pl.codehouse.restaurant.orders;


import java.util.List;

public record Order(
        int orderId,
        int customerId,
        List<MenuItem> menuItems
) {
    static Order from(OrderEntity savedEntity) {
        List<MenuItem> orderedItems = savedEntity.menuItems()
                .stream()
                .map(menuItemEntity -> new MenuItem(menuItemEntity.id(), menuItemEntity.name()))
                .toList();
        return new Order(savedEntity.id(), savedEntity.customerId(), orderedItems);
    }
}
