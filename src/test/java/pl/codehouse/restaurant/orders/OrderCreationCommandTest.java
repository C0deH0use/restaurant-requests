package pl.codehouse.restaurant.orders;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.codehouse.restaurant.Command;
import pl.codehouse.restaurant.Context;
import pl.codehouse.restaurant.ExecutionResult;
import pl.codehouse.restaurant.exceptions.ResourceNotFoundException;
import pl.codehouse.restaurant.exceptions.ResourceType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@DisplayName("OrderCreationCommand Tests")
@ExtendWith(MockitoExtension.class)
public class OrderCreationCommandTest {

    private static final int MENU_ITEM_1 = 10001;
    private static final int MENU_ITEM_2 = 10002;
    private static final int CUSTOMER_ID_1 = 1001;
    @Mock
    private OrderRepository repository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private OrderCreationCommand command;

    private Context<OrderRequest> context;

    @BeforeEach
    void setUp() {
        OrderRequest orderRequest = new OrderRequest(List.of(MENU_ITEM_1, MENU_ITEM_2), CUSTOMER_ID_1);
        context = new Context<>(orderRequest);
    }

    @Test
    @DisplayName("should create order successfully")
    void should_create_order_when_menu_items_found() {
        // Given
        MenuItemEntity item1 = new MenuItemEntity(MENU_ITEM_1, "Item 1", 1020, 1, false);
        MenuItemEntity item2 = new MenuItemEntity(MENU_ITEM_2, "Item 2", 1650, 1, false);
        OrderEntity orderEntity = new OrderEntity(1001, List.of(item1, item2), CUSTOMER_ID_1);

        when(menuItemRepository.findAllById(anyList())).thenReturn(Flux.just(item1, item2));
        when(repository.save(any(OrderEntity.class))).thenReturn(Mono.just(orderEntity));

        // When
        Mono<ExecutionResult<Order>> result = command.execute(context);

        // Then
        Order expectedOrder = Order.from(orderEntity);
        StepVerifier.create(result)
                .assertNext(executionResult -> {
                    assertThat(executionResult.isSuccess()).isTrue();
                    assertThat(executionResult.value()).hasValue(expectedOrder);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("should return failure when menu items not found")
    void should_return_failure_when_menu_items_not_found() {
        // Given
        RuntimeException exception = new RuntimeException("Test Exception");
        when(menuItemRepository.findAllById(anyList())).thenReturn(Flux.error(exception));

        // When
        Mono<ExecutionResult<Order>> result = command.execute(context);

        // Then
        StepVerifier.create(result)
                .expectNextCount(0)
                .expectErrorSatisfies(errorThrown -> assertThat(errorThrown)
                        .hasMessage("Test Exception")
                        .isInstanceOf(RuntimeException.class))
                .verify();
    }

    @Test
    @DisplayName("should throw ResourceNotFound exception when not all menu items are found")
    void should_throw_exception_when_not_all_menu_items_found() {
        // Given
        MenuItemEntity item1 = new MenuItemEntity(MENU_ITEM_1, "Item 1", 1020, 1, false);
        when(menuItemRepository.findAllById(anyList())).thenReturn(Flux.just(item1));

        // When
        Mono<ExecutionResult<Order>> result = command.execute(context);

        // Then
        StepVerifier.create(result)
                .expectErrorSatisfies(errorThrown -> assertThat(errorThrown)
                        .hasMessageContaining("Not all order components were found")
                        .hasFieldOrPropertyWithValue("resourceType", ResourceType.MENU_ITEM)
                        .isInstanceOf(ResourceNotFoundException.class))
                .verify();
    }
}