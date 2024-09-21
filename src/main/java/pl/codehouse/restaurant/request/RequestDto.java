package pl.codehouse.restaurant.request;


import java.util.List;

public record RequestDto(
        int orderId,
        int customerId,
        List<MenuItem> menuItems
) {
    static RequestDto from(RequestEntity savedEntity, List<MenuItemEntity> menuItems) {
        List<MenuItem> orderedItems = menuItems
                .stream()
                .map(menuItemEntity -> new MenuItem(menuItemEntity.id(), menuItemEntity.name()))
                .toList();
        return new RequestDto(savedEntity.id(), savedEntity.customerId(), orderedItems);
    }
}
