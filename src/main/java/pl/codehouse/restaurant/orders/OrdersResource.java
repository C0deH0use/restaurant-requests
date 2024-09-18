package pl.codehouse.restaurant.orders;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.codehouse.restaurant.Context;
import pl.codehouse.restaurant.ExecutionResult;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orders")
class OrdersResource {

    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final OrderCreationCommand orderCreationCommand;

    OrdersResource(MenuItemRepository menuItemRepository, OrderRepository orderRepository, OrderCreationCommand orderCreationCommand) {
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.orderCreationCommand = orderCreationCommand;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<Order> createRequest(@RequestBody OrderRequest request) {
        return orderCreationCommand.execute(new Context<>(request))
                .map(ExecutionResult::handle);
    }
}
