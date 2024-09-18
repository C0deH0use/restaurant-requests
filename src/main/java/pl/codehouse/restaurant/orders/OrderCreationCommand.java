package pl.codehouse.restaurant.orders;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import pl.codehouse.restaurant.Command;
import pl.codehouse.restaurant.Context;
import pl.codehouse.restaurant.ExecutionResult;
import pl.codehouse.restaurant.exceptions.ResourceNotFoundException;
import pl.codehouse.restaurant.exceptions.ResourceType;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Component
class OrderCreationCommand implements Command<OrderRequest, Order> {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OrderCreationCommand.class);
    private static final String MISSING_ORDER_COMPONENTS_ERROR_MESSAGE = "Not all order components were found";
    private final OrderRepository repository;
    private final MenuItemRepository menuItemRepository;

    OrderCreationCommand(OrderRepository repository, MenuItemRepository menuItemRepository) {
        this.repository = repository;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public Mono<ExecutionResult<Order>> execute(Context<OrderRequest> context) {
        LOGGER.info("Executing the creation of new Order for Customer: {} with Menu Items: {}", context.request().customerId(), context.request().menuItemId());
        return menuItemRepository.findAllById(context.request().menuItemId())
                .collectList()
                .flatMap(validateAndCreateNew(context))
                .map(Order::from)
                .map(ExecutionResult::success)
                .doOnError(exc -> ExecutionResult.failure(new RuntimeException("Error during OrderCreation command execution", exc)));
    }

    private Function<List<MenuItemEntity>, Mono<? extends OrderEntity>> validateAndCreateNew(Context<OrderRequest> context) {
        return selectedMenuItems -> {
            LOGGER.info("Validating customers new order menu items");
            if (selectedMenuItems.size() != context.request().menuItemId().size()) {
                LOGGER.error("Following items {} out of {} requested items are unknown", context.request().menuItemId().size() - selectedMenuItems.size(), context.request().menuItemId().size());
                return Mono.error(new ResourceNotFoundException(MISSING_ORDER_COMPONENTS_ERROR_MESSAGE, ResourceType.MENU_ITEM));
            }
            OrderEntity orderEntity = new OrderEntity(null, selectedMenuItems, context.request().customerId());
            return repository.save(orderEntity);
        };
    }
}
