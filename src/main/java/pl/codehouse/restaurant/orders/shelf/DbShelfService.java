package pl.codehouse.restaurant.orders.shelf;

import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import pl.codehouse.restaurant.orders.request.RequestMenuItem;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(value = "app.shelf.integration.type", havingValue = "db", matchIfMissing = true)
class DbShelfService implements ShelfService {
    private static final Logger log = LoggerFactory.getLogger(DbShelfService.class);

    private final Clock clock;
    private final ShelfRepository shelfRepository;

    DbShelfService(Clock clock, ShelfRepository shelfRepository) {
        this.clock = clock;
        this.shelfRepository = shelfRepository;
    }

    @Override
    public Mono<ShelfEntity> save(ShelfEntity shelfEntity) {
        return shelfRepository.save(shelfEntity);
    }

    @Override
    public Mono<ShelfEntity> findByMenuItem(RequestMenuItem menuItem) {
        log.info("Find Shelf by MenuItemId: {}", menuItem.menuItemId());

        return shelfRepository.findByMenuItemId(menuItem.menuItemId())
                .switchIfEmpty(Mono.defer(() -> {
                    ShelfEntity newShelf = createNewShelfItemFor(menuItem, clock);
                    log.info("Couldn't find Shelf by MenuItemId: {}. Creating new one: {}", menuItem.menuItemId(), newShelf);
                    return shelfRepository.save(newShelf);
                }));
    }
}
