package pl.codehouse.restaurant.request;

public record MenuItem(int menuId, String name) {

    static MenuItem from(MenuItemEntity entity) {
        return new MenuItem(entity.id(), entity.name());
    }
}
