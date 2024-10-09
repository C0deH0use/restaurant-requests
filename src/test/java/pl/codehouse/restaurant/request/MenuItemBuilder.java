package pl.codehouse.restaurant.request;

public class MenuItemBuilder {
    private MenuItemBuilder() {
    }

    private Integer menuId = 10001;
    private String name = "MENU ITEM";

    public static MenuItemBuilder aMenuItem() {
        return new MenuItemBuilder();
    }

    public MenuItemBuilder withMenuId(Integer menuId) {
        this.menuId = menuId;
        return this;
    }

    public MenuItemBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MenuItem build() {
        return new MenuItem(menuId, name);
    }
}
