package pl.codehouse.restaurant.request;

public class RequestedMenuItemPayloadBuilder {
    private RequestedMenuItemPayloadBuilder() {
    }

    private Integer menuId, quantity = 1;

    public static RequestedMenuItemPayloadBuilder aMenuItemRequestPayload() {
        return new RequestedMenuItemPayloadBuilder();
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
