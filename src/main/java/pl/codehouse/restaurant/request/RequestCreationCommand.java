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
class RequestCreationCommand implements Command<RequestPayload, RequestDto> {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RequestCreationCommand.class);
    private static final String MISSING_ORDER_COMPONENTS_ERROR_MESSAGE = "Not all request components were found";
    private final RequestRepository repository;
    private final MenuItemRepository menuItemRepository;
    private final RequestMenuItemRepository requestMenuItemRepository;

    RequestCreationCommand(RequestRepository repository, MenuItemRepository menuItemRepository, RequestMenuItemRepository requestMenuItemRepository) {
        this.repository = repository;
        this.menuItemRepository = menuItemRepository;
        this.requestMenuItemRepository = requestMenuItemRepository;
    }

    @Override
    @Transactional
    public Mono<ExecutionResult<RequestDto>> execute(Context<RequestPayload> context) {
        List<Integer> menuItems = context.request().menuItems();
        LOGGER.info("Executing the creation of new Request for Customer: {} with Menu Items: {}", context.request().customerId(), menuItems);

        return menuItemRepository.findAllById(menuItems)
                .collectList()
                .flatMap(validateAndCreateNew(context))
                .map(ExecutionResult::success)
                .doOnError(exc -> ExecutionResult.failure(new RuntimeException("Error during RequestCreation command execution", exc)));
    }

    private Function<List<MenuItemEntity>, Mono<RequestDto>> validateAndCreateNew(Context<RequestPayload> context) {
        return selectedMenuItems -> {
            LOGGER.info("Validating customers new request menu items");
            List<Integer> menuItems = context.request().menuItems();
            if (selectedMenuItems.size() != menuItems.size()) {
                LOGGER.error("Following items {} out of {} requested items are unknown", menuItems.size() - selectedMenuItems.size(), menuItems.size());
                return Mono.error(new ResourceNotFoundException(MISSING_ORDER_COMPONENTS_ERROR_MESSAGE, ResourceType.MENU_ITEM));
            }

            int customerId = context.request().customerId();
            LOGGER.info("Creating new Request for Customer: {} with Menu Items: {}", customerId, menuItems);
            return repository.save(new RequestEntity(0, customerId))
                    .flatMap(savedOrderEntity -> {
                        List<RequestMenuItemEntity> requestMenuItemEntities = selectedMenuItems.stream()
                                .map(menuItem -> new RequestMenuItemEntity(savedOrderEntity.id(), menuItem.id()))
                                .toList();
                        LOGGER.info("Storing Request MenuItems: {}", requestMenuItemEntities);
                        return requestMenuItemRepository.saveAll(requestMenuItemEntities)
                                .then()
                                .thenReturn(RequestDto.from(savedOrderEntity, selectedMenuItems));
                    });
        };
    }
}
