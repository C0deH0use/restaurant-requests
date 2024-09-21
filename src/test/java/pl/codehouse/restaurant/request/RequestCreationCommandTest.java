package pl.codehouse.restaurant.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@DisplayName("RequestCreationCommand Tests")
@ExtendWith(MockitoExtension.class)
public class RequestCreationCommandTest {

    private static final int MENU_ITEM_1 = 10001;
    private static final int MENU_ITEM_2 = 10002;
    private static final int CUSTOMER_ID_1 = 1001;
    @Mock
    private RequestRepository repository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RequestMenuItemRepository requestMenuItemRepository;

    @InjectMocks
    private RequestCreationCommand command;

    @Captor
    private ArgumentCaptor<List<RequestMenuItemEntity>> requestMenuItemEntityArgumentCaptor;

    private Context<RequestPayload> context;

    @BeforeEach
    void setUp() {
        RequestPayload requestPayload = new RequestPayload(List.of(MENU_ITEM_1, MENU_ITEM_2), CUSTOMER_ID_1);
        context = new Context<>(requestPayload);
    }

    @Test
    @DisplayName("should create request successfully")
    void should_create_request_given_menu_items_found() {
        // Given
        Integer expectedRequestId = 1;
        MenuItemEntity item1 = new MenuItemEntity(MENU_ITEM_1, "Item 1", 1020, 1, false);
        MenuItemEntity item2 = new MenuItemEntity(MENU_ITEM_2, "Item 2", 1650, 1, false);
        RequestEntity requestEntity = new RequestEntity(expectedRequestId, CUSTOMER_ID_1);

        given(menuItemRepository.findAllById(anyList())).willReturn(Flux.just(item1, item2));
        given(repository.save(any(RequestEntity.class))).willReturn(Mono.just(requestEntity));
        given(requestMenuItemRepository.saveAll(anyList())).willReturn(Flux.just());

        // When
        Mono<ExecutionResult<RequestDto>> result = command.execute(context);

        // Then
        RequestDto expectedRequest = RequestDto.from(requestEntity, List.of(item1, item2));
        StepVerifier.create(result)
                .assertNext(executionResult -> {
                    assertThat(executionResult.isSuccess()).isTrue();
                    assertThat(executionResult.value()).hasValue(expectedRequest);
                })
                .verifyComplete();

        // And
        then(requestMenuItemRepository).should(times(1)).saveAll(requestMenuItemEntityArgumentCaptor.capture());
        assertThat(requestMenuItemEntityArgumentCaptor.getValue())
                .hasSize(2)
                .allSatisfy(requestMenuItemEntity -> assertThat(expectedRequestId).isEqualTo(requestMenuItemEntity.requestId()))
                .extracting(RequestMenuItemEntity::menuItemId)
                .allSatisfy(menuItemId -> assertThat(List.of(MENU_ITEM_1, MENU_ITEM_2)).contains(menuItemId));
    }

    @Test
    @DisplayName("should return failure given menu items not found")
    void should_return_failure_given_menu_items_not_found() {
        // Given
        RuntimeException exception = new RuntimeException("Test Exception");
        given(menuItemRepository.findAllById(anyList())).willReturn(Flux.error(exception));

        // When
        Mono<ExecutionResult<RequestDto>> result = command.execute(context);

        // Then
        StepVerifier.create(result)
                .expectNextCount(0)
                .expectErrorSatisfies(errorThrown -> assertThat(errorThrown)
                        .hasMessage("Test Exception")
                        .isInstanceOf(RuntimeException.class))
                .verify();
    }

    @Test
    @DisplayName("should throw ResourceNotFound exception given not all menu items are found")
    void should_throw_exception_given_not_all_menu_items_found() {
        // Given
        MenuItemEntity item1 = new MenuItemEntity(MENU_ITEM_1, "Item 1", 1020, 1, false);
        given(menuItemRepository.findAllById(anyList())).willReturn(Flux.just(item1));

        // When
        Mono<ExecutionResult<RequestDto>> result = command.execute(context);

        // Then
        StepVerifier.create(result)
                .expectErrorSatisfies(errorThrown -> assertThat(errorThrown)
                        .hasMessageContaining("Not all request components were found")
                        .hasFieldOrPropertyWithValue("resourceType", ResourceType.MENU_ITEM)
                        .isInstanceOf(ResourceNotFoundException.class))
                .verify();
    }
}