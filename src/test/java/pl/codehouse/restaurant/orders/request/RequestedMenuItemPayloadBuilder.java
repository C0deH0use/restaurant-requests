package pl.codehouse.restaurant.orders.request;

import static pl.codehouse.restaurant.orders.request.MenuItemEntityBuilder.MENU_ITEM_1_ID;
import static pl.codehouse.restaurant.orders.request.MenuItemEntityBuilder.MENU_ITEM_2_ID;

public class RequestedMenuItemPayloadBuilder {
    private RequestedMenuItemPayloadBuilder() {
    }

    private Integer menuId, quantity = 1;

    public static RequestedMenuItemPayloadBuilder aMenuItemRequestPayload() {
        return new RequestedMenuItemPayloadBuilder();
    }

    public static RequestedMenuItemPayloadBuilder aMenuItemOneRequest() {
        return aMenuItemRequestPayload()
                .withMenuId(MENU_ITEM_1_ID)
                .withQuantity(1);
    }

    public static RequestedMenuItemPayloadBuilder aMenuItemTwoRequest() {
        return aMenuItemRequestPayload()
                .withMenuId(MENU_ITEM_2_ID)
                .withQuantity(1);
    }

    public RequestedMenuItemPayloadBuilder withMenuId(Integer menuId) {
        this.menuId = menuId;
        return this;
    }

    public RequestedMenuItemPayloadBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public RequestedMenuItemsPayload build() {
        return new RequestedMenuItemsPayload(menuId, quantity);
    }
}
