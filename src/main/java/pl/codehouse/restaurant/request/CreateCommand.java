package pl.codehouse.restaurant.request;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.codehouse.restaurant.Command;
import pl.codehouse.restaurant.Context;
import pl.codehouse.restaurant.ExecutionResult;
import pl.codehouse.restaurant.exceptions.ResourceNotFoundException;
import pl.codehouse.restaurant.exceptions.ResourceType;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Component
class CreateCommand implements Command<RequestPayload, RequestDto> {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CreateCommand.class);
    private static final String MISSING_ORDER_COMPONENTS_ERROR_MESSAGE = "Not all request components were found";
    private final RequestRepository repository;
    private final MenuItemRepository menuItemRepository;
    private final RequestMenuItemRepository requestMenuItemRepository;

    CreateCommand(RequestRepository repository, MenuItemRepository menuItemRepository, RequestMenuItemRepository requestMenuItemRepository) {
        this.repository = repository;
        this.menuItemRepository = menuItemRepository;
        this.requestMenuItemRepository = requestMenuItemRepository;
    }

    @Override
    @Transactional
    public Mono<ExecutionResult<RequestDto>> execute(Context<RequestPayload> context) {
        List<RequestedMenuItemsPayload> menuItems = context.request().menuItems();
        LOGGER.info("Executing the creation of new Request for Customer: {} with Menu Items: {}", context.request().customerId(), menuItems);

        List<Integer> menuItemIds = menuItems.stream().map(RequestedMenuItemsPayload::menuId).toList();
        return menuItemRepository.findAllById(menuItemIds)
                .collectList()
                .flatMap(validateAndCreateNew(context))
                .map(ExecutionResult::success)
                .doOnError(exc -> ExecutionResult.failure(new RuntimeException("Error during RequestCreation command execution", exc)));
    }

    private Function<List<MenuItemEntity>, Mono<RequestDto>> validateAndCreateNew(Context<RequestPayload> context) {
        return selectedMenuItems -> {
            LOGGER.info("Validating customers new request menu items");
            List<RequestedMenuItemsPayload> requestedMenuItems = context.request().menuItems();
            if (selectedMenuItems.size() != requestedMenuItems.size()) {
                LOGGER.error("Following items {} out of {} requested items are unknown", requestedMenuItems.size() - selectedMenuItems.size(), requestedMenuItems.size());
                return Mono.error(new ResourceNotFoundException(MISSING_ORDER_COMPONENTS_ERROR_MESSAGE, ResourceType.MENU_ITEM));
            }

            int customerId = context.request().customerId();
            LOGGER.info("Creating new Request for Customer: {} with Menu Items: {}", customerId, requestedMenuItems);
            return repository.save(RequestEntity.newRequestFor(customerId))
                    .zipWhen(savedOrderEntity -> {
                        List<RequestMenuItemEntity> requestMenuItemEntities = selectedMenuItems.stream()
                                .map(createMenuItemEntity(savedOrderEntity, requestedMenuItems))
                                .toList();
                        LOGGER.info("Storing Request MenuItems: {}", requestMenuItemEntities);
                        return requestMenuItemRepository.saveAll(requestMenuItemEntities).collectList();
                    })
                    .map(tuple2 -> RequestDto.from(tuple2.getT1(), tuple2.getT2(), selectedMenuItems));
        };
    }

    private static Function<MenuItemEntity, RequestMenuItemEntity> createMenuItemEntity(RequestEntity savedOrderEntity, List<RequestedMenuItemsPayload> menuItems) {
        return menuItem -> {
            int menuItemQuantity = menuItems.stream()
                    .filter(requestedMenuItem -> menuItem.id() == requestedMenuItem.menuId())
                    .findFirst()
                    .map(RequestedMenuItemsPayload::quantity)
                    .orElseThrow(() -> new ResourceNotFoundException("Unable to match Menu Item by Id: " + menuItem.id(), ResourceType.MENU_ITEM));
            return new RequestMenuItemEntity(savedOrderEntity.id(), menuItem.id(), menuItemQuantity, 0, menuItem.immediate());
        };
    }
}
