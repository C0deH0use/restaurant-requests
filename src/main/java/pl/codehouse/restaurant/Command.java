package pl.codehouse.restaurant;

import pl.codehouse.restaurant.orders.Order;
import reactor.core.publisher.Mono;

public interface Command<T, R> {
    Mono<ExecutionResult<Order>> execute(Context<T> context);
}
