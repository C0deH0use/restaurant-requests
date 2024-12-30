package pl.codehouse.restaurant.orders.request;

record MenuItem(int menuId, String name) {

    static MenuItem from(MenuItemEntity entity) {
        return new MenuItem(entity.id(), entity.name());
    }
}
