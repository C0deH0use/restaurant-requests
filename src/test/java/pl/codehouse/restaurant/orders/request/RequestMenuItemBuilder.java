
package pl.codehouse.restaurant.orders.request;

import static pl.codehouse.restaurant.orders.request.MenuItemEntityBuilder.MENU_ITEM_1_ID;
import static pl.codehouse.restaurant.orders.request.MenuItemEntityBuilder.MENU_ITEM_1_NAME;
import static pl.codehouse.restaurant.orders.request.MenuItemEntityBuilder.MENU_ITEM_2_ID;
import static pl.codehouse.restaurant.orders.request.MenuItemEntityBuilder.MENU_ITEM_2_NAME;
import static pl.codehouse.restaurant.orders.request.RequestMenuItemEntityBuilder.REQUEST_MENU_ITEM_1_ID;
import static pl.codehouse.restaurant.orders.request.RequestMenuItemEntityBuilder.REQUEST_MENU_ITEM_2_ID;

public class RequestMenuItemBuilder {

    private RequestMenuItemBuilder() {
        id = REQUEST_MENU_ITEM_1_ID;
        menuId = MENU_ITEM_1_ID;
        menuItemName = MENU_ITEM_2_NAME;
        quantity = 1;
        prepared = 0;
        immediate = false;
    }

    private Integer id, menuId, quantity, prepared;
    private String menuItemName;
    private boolean immediate;

    public static RequestMenuItemBuilder aMenuItemsRequest() {
        return new RequestMenuItemBuilder();
    }

    public static RequestMenuItemBuilder aRequestMenuItemOne() {
        return aMenuItemsRequest()
                .withId(REQUEST_MENU_ITEM_1_ID)
                .withMenuId(MENU_ITEM_1_ID)
                .withMenuItemName(MENU_ITEM_1_NAME);
    }

    public static RequestMenuItemBuilder aRequestMenuItemTwo() {
        return aMenuItemsRequest()
                .withId(REQUEST_MENU_ITEM_2_ID)
                .withMenuId(MENU_ITEM_2_ID)
                .withMenuItemName(MENU_ITEM_2_NAME);
    }

    public RequestMenuItemBuilder withId(Integer id) {
        this.id = id;
        return this;
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
        return new RequestMenuItem(id, menuId, menuItemName, quantity, prepared, immediate);
    }
}
