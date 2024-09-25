package pl.codehouse.restaurant.request;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class RequestStepDefinitions {
    private static final int REQUEST_ID = 1;
    private static final int VALUE_REPRESENTING_NULL_ID = 0;
    private static final int MENU_ITEM_1 = 10001;
    private static final int MENU_ITEM_2 = 10002;
    private static final int UNKNOWN_MENU_ITEM_3 = 1002;
    private static final int CUSTOMER_ID_1 = 1001;

    private final RequestRepository repository = Mockito.mock(RequestRepository.class);

    private final MenuItemRepository menuItemRepository = Mockito.mock(MenuItemRepository.class);

    private final RequestMenuItemRepository requestMenuItemRepository = Mockito.mock(RequestMenuItemRepository.class);

    private final RequestCreationCommand command = new RequestCreationCommand(repository, menuItemRepository, requestMenuItemRepository);

    private Context<RequestPayload> context;

    private Mono<ExecutionResult<RequestDto>> result;

    private RequestDto expectedRequest;
    private final MenuItemEntity item1 = new MenuItemEntity(MENU_ITEM_1, "Item 1", 1020, 1, false);
    private final MenuItemEntity item2 = new MenuItemEntity(MENU_ITEM_2, "Item 2", 1650, 1, false);

    private final RequestEntity requestEntity = new RequestEntity(REQUEST_ID, CUSTOMER_ID_1);

    @Given("customer requests known menu items")
    public void customerRequestsKnownMenuItems() {
        expectedRequest = RequestDto.from(requestEntity, List.of(item1, item2));

        RequestPayload requestPayload = new RequestPayload(List.of(MENU_ITEM_1, MENU_ITEM_2), CUSTOMER_ID_1);
        context = new Context<>(requestPayload);

        given(menuItemRepository.findAllById(anyList())).willReturn(Flux.just(item1, item2));
        given(repository.save(any(RequestEntity.class))).willReturn(Mono.just(requestEntity));
        given(requestMenuItemRepository.saveAll(anyList())).willReturn(Flux.just());
    }

    @Given("customer requests any of the menu items not being known to the restaurant")
    public void customerRequestsAnyOfTheMenuItemsNotBeingKnownToTheRestaurant() {
        RequestPayload requestPayload = new RequestPayload(List.of(MENU_ITEM_1, UNKNOWN_MENU_ITEM_3), CUSTOMER_ID_1);
        context = new Context<>(requestPayload);

        given(menuItemRepository.findAllById(anyList())).willReturn(Flux.just(item1));
    }

    @When("creating new request")
    public void creatingNewRequest() {
       result = command.execute(context);
    }

    @Then("new request is created")
    public void newRequestCreated() {
        StepVerifier.create(result)
                .assertNext(executionResult -> {
                    assertThat(executionResult.isSuccess()).isTrue();
                    assertThat(executionResult.value()).hasValue(expectedRequest);
                })
                .verifyComplete();
        ArgumentCaptor<RequestEntity> requestEntityArgumentCaptor = ArgumentCaptor.captor();
        ArgumentCaptor<List<RequestMenuItemEntity>> requestMenuItemEntityArgumentCaptor = ArgumentCaptor.captor();


        then(repository).should(times(1)).save(requestEntityArgumentCaptor.capture());
        then(requestMenuItemRepository).should(times(1)).saveAll(requestMenuItemEntityArgumentCaptor.capture());

        // And
        assertThat(requestEntityArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("id", VALUE_REPRESENTING_NULL_ID)
                .hasFieldOrPropertyWithValue("customerId", requestEntity.customerId());

        assertThat(requestMenuItemEntityArgumentCaptor.getValue())
                .hasSize(2)
                .allSatisfy(requestMenuItemEntity -> assertThat(requestMenuItemEntity.requestId()).isEqualTo(REQUEST_ID))
                .extracting(RequestMenuItemEntity::menuItemId)
                .allSatisfy(menuItemId -> assertThat(List.of(MENU_ITEM_1, MENU_ITEM_2)).contains(menuItemId));
    }

    @Then("no request is created")
    public void noRequestCreated() {
        StepVerifier.create(result)
                .expectErrorSatisfies(errorThrown -> assertThat(errorThrown)
                        .hasMessageContaining("Not all request components were found")
                        .hasFieldOrPropertyWithValue("resourceType", ResourceType.MENU_ITEM)
                        .isInstanceOf(ResourceNotFoundException.class))
                .verify();

        then(repository).should(never()).save(any());
        then(requestMenuItemRepository).should(never()).saveAll(anyList());
    }
}
