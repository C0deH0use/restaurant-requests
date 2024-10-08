package pl.codehouse.restaurant.request;

import static pl.codehouse.restaurant.request.RequestDtoBuilder.REQUEST_ID;
import static pl.codehouse.restaurant.request.RequestMenuItemBuilder.MENU_ITEM_1;
import static pl.codehouse.restaurant.request.RequestMenuItemBuilder.MENU_ITEM_2;

public class RequestMenuItemEntityBuilder {
    private RequestMenuItemEntityBuilder() {
        requestId = REQUEST_ID;
        menuId = MENU_ITEM_1;
        quantity = 1;
        prepared = 0;
        immediate = false;
    }

    private Integer requestId, menuId, quantity, prepared;
    private boolean immediate;

    public static RequestMenuItemEntityBuilder aMenuItemsRequest() {
        return new RequestMenuItemEntityBuilder();
    }

    public static RequestMenuItemEntityBuilder aMenuItemRequestOne() {
        return aMenuItemsRequest()
                .withMenuId(MENU_ITEM_1);
    }
    public static RequestMenuItemEntityBuilder aMenuItemRequestTwo() {
        return aMenuItemsRequest()
                .withMenuId(MENU_ITEM_2);
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
        return new RequestMenuItemEntity(requestId, menuId, quantity, prepared, immediate);
    }
}
