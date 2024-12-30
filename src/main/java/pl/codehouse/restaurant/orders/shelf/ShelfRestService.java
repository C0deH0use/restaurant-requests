package pl.codehouse.restaurant.orders.shelf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import pl.codehouse.restaurant.orders.request.RequestMenuItem;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(value = "app.shelf.integration.type", havingValue = "rest")
class ShelfRestService implements ShelfService {
    private static final Logger log = LoggerFactory.getLogger(ShelfRestService.class);

    @Override
    public Mono<ShelfEntity> save(ShelfEntity shelfEntity) {
        return Mono.empty();
    }

    @Override
    public Mono<ShelfEntity> findByMenuItem(RequestMenuItem menuItem) {
        return Mono.empty();
    }
}
