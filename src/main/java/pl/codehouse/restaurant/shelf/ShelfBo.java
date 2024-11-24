package pl.codehouse.restaurant.shelf;

import java.time.Clock;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.codehouse.restaurant.exceptions.ResourceNotFoundException;
import pl.codehouse.restaurant.request.RequestMenuItem;
import reactor.core.publisher.Mono;

/**
 * Business Object class for managing shelf operations in the restaurant system.
 * This service handles the logic for taking items from the shelf and updating inventory.
 */
@Service
public class ShelfBo {
    private static final Logger logger = LoggerFactory.getLogger(ShelfBo.class);
    private final Clock clock;
    private final ShelfRepository repository;
    private final KitchenWorkerRequestPublisher workerRequestPublisher;

    /**
     * Constructs a new ShelfBO with the specified clock and repository.
     *
     * @param clock The clock to use for timestamp operations.
     * @param repository The repository for shelf entity operations.
     */
    ShelfBo(Clock clock, ShelfRepository repository,
            KitchenWorkerRequestPublisher workerRequestPublisher) {
        this.clock = clock;
        this.repository = repository;
        this.workerRequestPublisher = workerRequestPublisher;
    }

    /**
     * Attempts to take a requested menu item from the shelf.
     * This method handles the logic for updating shelf quantities and requesting new items if necessary.
     *
     * @param menuItem The requested menu item to take from the shelf.
     * @return A Mono emitting a ShelfTakeResult indicating the result of the operation.
     * @throws ResourceNotFoundException if the shelf entity for the menu item is missing.
     * @throws IllegalArgumentException if the requested quantity is not positive.
     */
    public Mono<ShelfTakeResult> take(RequestMenuItem menuItem) {
        logger.info("Collecting {} menu items [{} ==> {}] as requested by customer", menuItem.remainingItems(), menuItem.menuItemId(), menuItem.menuItemName());
        if (menuItem.remainingItems() <= 0) {
            logger.error("For the following requested menu item [{}: {}], amount need to be greater than zero", menuItem.menuItemName(), menuItem.menuItemId());
            return Mono.error(new IllegalArgumentException("Requested menu item amount need to be greater than zero"));
        }

        Mono<ShelfEntity> entityMono = repository.findByMenuItemId(menuItem.menuItemId())
                .switchIfEmpty(Mono.defer(() -> repository.save(createNewShelfItemFor(menuItem))));

        return entityMono
                .flatMap(shelfEntity -> takeFromShelfOrRequestFromKitchen(menuItem, shelfEntity));
    }

    private Mono<ShelfTakeResult> takeFromShelfOrRequestFromKitchen(RequestMenuItem menuItem, ShelfEntity shelfEntity) {
        if (shelfEntity.quantity() < menuItem.remainingItems()) {
            logger.info("On the Shelf, the menu items of {}: {} have less items then requested in order - {}, requested: {}",
                    shelfEntity.itemName(), shelfEntity.menuItemId(), shelfEntity.quantity(), menuItem.remainingItems());
            int quantityToRequest = menuItem.remainingItems() - shelfEntity.quantity();
            workerRequestPublisher.publishRequest(menuItem, quantityToRequest);
            ShelfEntity updateEntity = shelfEntity.withQuantityUpdate(0, LocalDateTime.now(clock));

            return repository.save(updateEntity)
                    .thenReturn(new ShelfTakeResult(PackingStatus.REQUESTED_ITEMS, shelfEntity.quantity()));
        }

        int itemsTaken = shelfEntity.quantity() - menuItem.remainingItems();
        ShelfEntity updateEntity = shelfEntity.withQuantityUpdate(itemsTaken, LocalDateTime.now(clock));

        return repository.save(updateEntity)
                .thenReturn(new ShelfTakeResult(PackingStatus.READY_TO_COLLECT, menuItem.remainingItems()));
    }

    /**
     * Creates a new ShelfEntity for a given RequestMenuItem.
     * This method is used when a shelf item doesn't exist for a requested menu item.
     *
     * @param menuItem The RequestMenuItem to create a new ShelfEntity for.
     * @return A new ShelfEntity instance.
     */
    private ShelfEntity createNewShelfItemFor(RequestMenuItem menuItem) {
        return new ShelfEntity(0, menuItem.menuItemName(), menuItem.menuItemId(), 0, 0, LocalDateTime.now(clock));
    }
}
