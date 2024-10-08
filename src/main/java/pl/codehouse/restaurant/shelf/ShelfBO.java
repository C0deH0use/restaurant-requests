package pl.codehouse.restaurant.shelf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.codehouse.restaurant.exceptions.ResourceNotFoundException;
import pl.codehouse.restaurant.exceptions.ResourceType;
import pl.codehouse.restaurant.request.RequestMenuItem;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class ShelfBO {
    private static final Logger logger = LoggerFactory.getLogger(ShelfBO.class);
    private final Clock clock;
    private final ShelfRepository repository;

    ShelfBO(Clock clock, ShelfRepository repository) {
        this.clock = clock;
        this.repository = repository;
    }

    public Mono<ShelfTakeResult> take(RequestMenuItem menuItem) {
        logger.info("Collecting {} menu items [{} ==> {}] as requested by customer", menuItem.remainingItems(), menuItem.menuItemId(), menuItem.menuItemName());
        ShelfEntity block = repository.findByMenuItemId(menuItem.menuItemId())
                .defaultIfEmpty(createNewShelfItemFor(menuItem))
                .blockOptional()
                .orElseThrow((() -> new ResourceNotFoundException("Shelf Entity for the following menu item: %s is missing".formatted(menuItem.menuItemId()), ResourceType.SHELF_ITEM)));

        if (menuItem.remainingItems() <= 0) {
            logger.error("For the following requested menu item [{}: {}], amount need to be greater than zero", menuItem.menuItemName(), menuItem.menuItemId());
            throw new IllegalArgumentException("Requested menu item amount need to be greater than zero");
        }

        if (block.quantity() < menuItem.remainingItems()) {
            logger.info("On the Shelf, the menu items of {}: {} have less items then requested in order - {}, requested: {}",
                    block.itemName(), block.menuItemId(), block.quantity(), menuItem.remainingItems());
            int quantityToRequest = menuItem.remainingItems() - block.quantity();
            logger.info("Requesting Kitchen to create {} new menu items {}", quantityToRequest, block.itemName());
            // send request to kitchen
            ShelfEntity updateEntity = block.withQuantityUpdate(0, LocalDateTime.now(clock));
            return repository.save(updateEntity)
                    .thenReturn(new ShelfTakeResult(PackingStatus.REQUESTED_ITEMS, block.quantity()));
        }

        int itemsTaken = block.quantity() - menuItem.remainingItems();
        ShelfEntity updateEntity = block.withQuantityUpdate(itemsTaken, LocalDateTime.now(clock));
        return repository.save(updateEntity)
                .thenReturn(new ShelfTakeResult(PackingStatus.READY_TO_COLLECT,  menuItem.remainingItems()));

    }

    private ShelfEntity createNewShelfItemFor(RequestMenuItem menuItem) {
        return new ShelfEntity(-1, menuItem.menuItemName(), menuItem.menuItemId(), 0, 0, LocalDateTime.now(clock));
    }
}
