
package pl.codehouse.restaurant.request;

public class RequestMenuItemBuilder {
    public static final int MENU_ITEM_1 = 10001;
    public static final String MENU_ITEM_1_NAME = "Default MenuItem 1";
    public static final int MENU_ITEM_2 = 10012;
    public static final String MENU_ITEM_2_NAME = "Default MenuItem 2";

    private RequestMenuItemBuilder() {
        menuId = MENU_ITEM_1;
        menuItemName = MENU_ITEM_1_NAME;
        quantity = 1;
        prepared = 0;
        immediate = false;
    }

    private Integer menuId, quantity, prepared;
    private String menuItemName;
    private boolean immediate;

    public static RequestMenuItemBuilder aMenuItemsRequest() {
        return new RequestMenuItemBuilder();
    }

    public static RequestMenuItemBuilder aRequestMenuItemOne() {
        return aMenuItemsRequest()
                .withMenuId(MENU_ITEM_1)
                .withMenuItemName(MENU_ITEM_1_NAME);
    }

    public static RequestMenuItemBuilder aRequestMenuItemTwo() {
        return aMenuItemsRequest()
                .withMenuId(MENU_ITEM_2)
                .withMenuItemName(MENU_ITEM_2_NAME);
    }

    public RequestMenuItemBuilder withMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
        return this;
    }

    public RequestMenuItemBuilder withMenuId(Integer menuId) {
        this.menuId = menuId;
        return this;
    }

    public RequestMenuItemBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public RequestMenuItemBuilder withPrepared(Integer prepared) {
        this.prepared = prepared;
        return this;
    }

    public RequestMenuItemBuilder isImmediate() {
        this.immediate = true;
        return this;
    }

    public RequestMenuItem build() {
        return new RequestMenuItem(menuId, menuItemName, quantity, prepared, immediate);
    }
}
