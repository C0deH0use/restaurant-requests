package pl.codehouse.restaurant.request;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Represents a menu item within a specific request in the restaurant system.
 * This entity is mapped to the "request_menu_item" table in the database.
 */
@Table("request_menu_item")
record RequestMenuItemEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE) int id,
        int requestId,
        int menuItemId,
        int quantity,
        int prepared,
        boolean immediate) {

    /**
     * Creates a new instance of RequestMenuItemEntity with default values.
     *
     * @param requestId The ID of the request this menu item belongs to.
     * @param menuItemId The ID of the menu item.
     * @param quantity The quantity of this menu item in the request.
     * @param immediate Whether this item requires immediate preparation.
     * @return A new RequestMenuItemEntity instance.
     */
    static RequestMenuItemEntity newInstance(int requestId,
                                                    int menuItemId,
                                                    int quantity,
                                                    boolean immediate) {
        return new RequestMenuItemEntity(0, requestId, menuItemId, quantity, 0, immediate);
    }

    /**
     * Creates a new instance with an updated prepared count.
     *
     * @param collectedQuantity The additional quantity that has been prepared.
     * @return A new RequestMenuItemEntity with the updated prepared count.
     */
    RequestMenuItemEntity withUpdatedPreparedCnt(int collectedQuantity) {
        return new RequestMenuItemEntity(id, requestId, menuItemId, quantity, prepared + collectedQuantity, immediate);
    }

    /**
     * Checks if the preparation of this menu item is finished.
     *
     * @return true if the prepared quantity equals the total quantity, false otherwise.
     */
    boolean isFinished() {
        return quantity == prepared;
    }
}
