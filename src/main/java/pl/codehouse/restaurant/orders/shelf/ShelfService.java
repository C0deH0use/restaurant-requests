package pl.codehouse.restaurant.orders.shelf;

import java.time.Clock;
import java.time.LocalDateTime;
import pl.codehouse.restaurant.orders.request.RequestMenuItem;
import reactor.core.publisher.Mono;

/**
 * Shelf related services.
 */
interface ShelfService {
    /**
     * Store new Shelf entity.
     *
     * @param shelfEntity parameter containing all shelf values.
     * @return newly stored Shelf Entity.
     */
    Mono<ShelfEntity> save(ShelfEntity shelfEntity);

    /**
     * Find Shelf Entity by MenuItemId.
     * In case if given Menu Item does not exist it, one should be created.
     *
     * @param menuItem to extract Menu Item ID or Menu Item Name and Id in case when one does not exist yet.
     * @return ShelfEntity by given menu item id.
     */
    Mono<ShelfEntity> findByMenuItem(RequestMenuItem menuItem);


    /**
     * Creates a new ShelfEntity for a given RequestMenuItem.
     * This method is used when a shelf item doesn't exist for a requested menu item.
     *
     * @param menuItem The RequestMenuItem to create a new ShelfEntity for.
     * @return A new ShelfEntity instance.
     */
    default ShelfEntity createNewShelfItemFor(RequestMenuItem menuItem, Clock clock) {
        return new ShelfEntity(0, menuItem.menuItemName(), menuItem.menuItemId(), 0, 0, LocalDateTime.now(clock));
    }
}
