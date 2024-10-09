package pl.codehouse.restaurant.request;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("request_menu_item")
public record RequestMenuItemEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE) int id,
        int requestId,
        int menuItemId,
        int quantity,
        int prepared,
        boolean immediate) {

    public static RequestMenuItemEntity newInstance(int requestId,
                                                    int menuItemId,
                                                    int quantity,
                                                    boolean immediate) {
        return new RequestMenuItemEntity(0, requestId, menuItemId, quantity, 0, immediate);
    }

    public RequestMenuItemEntity withUpdatedPreparedCnt(int collectedQuantity) {
        return new RequestMenuItemEntity(id, requestId, menuItemId, quantity, prepared + collectedQuantity, immediate);
    }

    public boolean isFinished() {
        return quantity == prepared;
    }
}
