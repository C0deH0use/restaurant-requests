package pl.codehouse.restaurant.request;

import org.springframework.data.relational.core.mapping.Table;

@Table("request_menu_item")
public record RequestMenuItemEntity(
        int requestId,
        int menuItemId,
        int quantity,
        int prepared,
        boolean immediate) {

    public RequestMenuItemEntity withUpdatedPreparedCnt(int collectedQuantity) {
        return new RequestMenuItemEntity(requestId, menuItemId, quantity, prepared + collectedQuantity, immediate);
    }

    public boolean isFinished() {
        return quantity == prepared;
    }
}
