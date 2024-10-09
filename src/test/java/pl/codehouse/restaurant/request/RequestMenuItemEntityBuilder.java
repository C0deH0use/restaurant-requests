package pl.codehouse.restaurant.request;

import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.MENU_ITEM_1_ID;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.MENU_ITEM_2_ID;
import static pl.codehouse.restaurant.request.RequestEntityBuilder.REQUEST_ID;

public class RequestMenuItemEntityBuilder {
    public static final int REQUEST_MENU_ITEM_1_ID = 10101;
    public static final int REQUEST_MENU_ITEM_2_ID = 10201;
    private RequestMenuItemEntityBuilder() {
        id = REQUEST_MENU_ITEM_1_ID;
        requestId = REQUEST_ID;
        menuId = MENU_ITEM_1_ID;
        quantity = 1;
        prepared = 0;
        immediate = false;
    }

    private Integer id, requestId, menuId, quantity, prepared;
    private boolean immediate;

    public static RequestMenuItemEntityBuilder aRequestMenuItems() {
        return new RequestMenuItemEntityBuilder();
    }

    public static RequestMenuItemEntityBuilder aRequestMenuItemEntityOne() {
        return aRequestMenuItems()
                .withId(REQUEST_MENU_ITEM_1_ID)
                .withMenuId(MENU_ITEM_1_ID);
    }
    public static RequestMenuItemEntityBuilder aRequestMenuItemEntityTwo() {
        return aRequestMenuItems()
                .withId(REQUEST_MENU_ITEM_2_ID)
                .withMenuId(MENU_ITEM_2_ID);
    }

    public RequestMenuItemEntityBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public RequestMenuItemEntityBuilder withRequestId(Integer requestId) {
        this.requestId = requestId;
        return this;
    }

    public RequestMenuItemEntityBuilder withMenuId(Integer menuId) {
        this.menuId = menuId;
        return this;
    }

    public RequestMenuItemEntityBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public RequestMenuItemEntityBuilder withPrepared(Integer prepared) {
        this.prepared = prepared;
        return this;
    }

    public RequestMenuItemEntityBuilder isImmediate() {
        this.immediate = true;
        return this;
    }

    public RequestMenuItemEntity build() {
        return new RequestMenuItemEntity(id, requestId, menuId, quantity, prepared, immediate);
    }
}
