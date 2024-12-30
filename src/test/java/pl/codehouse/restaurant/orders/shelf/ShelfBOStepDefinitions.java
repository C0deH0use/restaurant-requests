package pl.codehouse.restaurant.orders.shelf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static pl.codehouse.restaurant.orders.request.MenuItemEntityBuilder.MENU_ITEM_1_ID;
import static pl.codehouse.restaurant.orders.request.MenuItemEntityBuilder.MENU_ITEM_1_NAME;
import static pl.codehouse.restaurant.orders.request.MenuItemEntityBuilder.MENU_ITEM_2_ID;
import static pl.codehouse.restaurant.orders.request.MenuItemEntityBuilder.MENU_ITEM_2_NAME;
import static pl.codehouse.restaurant.orders.request.RequestMenuItemBuilder.aRequestMenuItemOne;
import static pl.codehouse.restaurant.orders.request.RequestMenuItemBuilder.aRequestMenuItemTwo;
import static pl.codehouse.restaurant.orders.shelf.ShelfBuilder.aShelf;

import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.apache.commons.lang3.BooleanUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pl.codehouse.restaurant.orders.request.RequestMenuItem;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ShelfBOStepDefinitions {
    private static final long UPDATED_VERSION_VALUE = 1L;

    private final Clock clock = Clock.fixed(Instant.parse("2024-10-05T15:15:30.00Z"), ZoneOffset.UTC);
    private final LocalDateTime updatedAt = LocalDateTime.now(clock);

    private ShelfService shelfService;
    private KitchenWorkerRequestPublisher workerRequestPublisher;
    private ArgumentCaptor<ShelfEntity> shelfEntityArgumentCaptor;
    private ArgumentCaptor<RequestMenuItem> requestMenuItemArgumentCaptor;
    private Shelf sut;

    private Mono<ShelfTakeResult> executionResult;

    @Before
    public void setUp() {
        shelfService = Mockito.mock(ShelfService.class);
        workerRequestPublisher = Mockito.mock(KitchenWorkerRequestPublisher.class);
        shelfEntityArgumentCaptor = ArgumentCaptor.forClass(ShelfEntity.class);
        requestMenuItemArgumentCaptor = ArgumentCaptor.forClass(RequestMenuItem.class);
        sut = new Shelf(clock, shelfService, workerRequestPublisher);
    }

    @Given("the shelf contains {int} menu items from request")
    public void given_ShelfContainsXMenuItemsFromRequest(int shelfItems) {
        given(shelfService.save(any())).willReturn(Mono.just(aShelf().build()));

        ShelfEntity shelfEntity = aShelf()
                .aShelfWithAvailableMenuItems()
                .withMenuId(MENU_ITEM_1_ID)
                .withName(MENU_ITEM_1_NAME)
                .withItemsQuantity(shelfItems)
                .build();
        given(shelfService.findByMenuItem(any(RequestMenuItem.class))).willReturn(Mono.defer(() -> Mono.just(shelfEntity)));
    }

    @Given("shelf not containing any items")
    public void shelfNotContainingAnyItems() {
        ShelfEntity shelfEntity = aShelf()
                .newShelfEntity()
                .withMenuId(MENU_ITEM_2_ID)
                .withName(MENU_ITEM_2_NAME)
                .withItemsQuantity(0)
                .build();
        given(shelfService.findByMenuItem(any(RequestMenuItem.class))).willReturn(Mono.just(shelfEntity));
        given(shelfService.save(any())).willAnswer(invocation -> {
            ShelfEntity savedEntity = invocation.getArgument(0);
            return Mono.just(savedEntity);
        });
    }

    @When("handling requested {int} Menu Items")
    public void when_RequestWithXMenuItems(int menuItemsRequested) {
        RequestMenuItem requestMenuItem = aRequestMenuItemOne().withQuantity(menuItemsRequested).build();
        executionResult = Mono.defer(() -> sut.take(requestMenuItem));
    }

    @When("request a new Menu Item")
    public void requestANewMenuItem() {
        RequestMenuItem requestMenuItem = aRequestMenuItemTwo().build();
        executionResult = sut.take(requestMenuItem);
    }

    @Then("request should be updated with {int} prepared menu items from the shelf")
    public void then_RequestShouldBeUpdatedWithXPreparedMenuItemsFromTheShelf(int collectedItems) {
        StepVerifier.create(executionResult)
                .assertNext(result -> assertThat(result.itemsTakenFromShelf()).isEqualTo(collectedItems))
                .verifyComplete();
    }

    @Then("new shelf item should be created with {int} menu items")
    public void newShelfItemShouldBeCreatedWithMenuItems(int expectedQuantity) {
        then(shelfService).should(times(1)).findByMenuItem(requestMenuItemArgumentCaptor.capture());
        then(shelfService).should(times(1)).save(shelfEntityArgumentCaptor.capture());

        assertThat(requestMenuItemArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("menuItemId", MENU_ITEM_2_ID)
                .hasFieldOrPropertyWithValue("menuItemName", MENU_ITEM_2_NAME);

        assertThat(shelfEntityArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("menuItemId", MENU_ITEM_2_ID)
                .hasFieldOrPropertyWithValue("itemName", MENU_ITEM_2_NAME)
                .hasFieldOrPropertyWithValue("quantity", expectedQuantity)
                .hasFieldOrPropertyWithValue("updatedAt", updatedAt)
                .hasFieldOrPropertyWithValue("version", UPDATED_VERSION_VALUE);
    }

    @And("shelf should be updated with {int} menu items taken for request")
    public void and_ShelfShouldBeUpdatedWithXMenuItemsTakenForRequestAndVersionShouldBeUpdated(int collectedItems) {
        then(shelfService).should(times(1)).findByMenuItem(requestMenuItemArgumentCaptor.capture());
        then(shelfService).should(times(1)).save(shelfEntityArgumentCaptor.capture());

        assertThat(requestMenuItemArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("menuItemId", MENU_ITEM_1_ID)
                .hasFieldOrPropertyWithValue("menuItemName", MENU_ITEM_1_NAME);

        long expectedVersion = 2;
        assertThat(shelfEntityArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("menuItemId", MENU_ITEM_1_ID)
                .hasFieldOrPropertyWithValue("itemName", MENU_ITEM_1_NAME)
                .hasFieldOrPropertyWithValue("quantity", collectedItems)
                .hasFieldOrPropertyWithValue("version", expectedVersion)
                .hasFieldOrPropertyWithValue("updatedAt", updatedAt);
    }

    @And("request Status should be set to {packingStatus}")
    public void requestStatusShouldBeSetToExpectedStatus(PackingStatus expectedStatus) {
        StepVerifier.create(executionResult)
                .assertNext(result -> assertThat(result.packingStatus()).isEqualTo(expectedStatus))
                .verifyComplete();
    }

    @And("{int} menu items should be requested by the restaurant worker")
    public void and_XMenuItemsShouldBeRequestedByTheRestaurantWorker(int menuItemsRequested) {
        if (menuItemsRequested == 0) {
            then(workerRequestPublisher).should(never()).publishRequest(any(), nullable(Integer.class));
            return;
        }
        then(workerRequestPublisher).should(times(1)).publishRequest(any(RequestMenuItem.class), eq(menuItemsRequested));
    }

    @ParameterType("READY_TO_COLLECT|REQUESTED_ITEMS")
    public PackingStatus packingStatus(String status) {
        return PackingStatus.valueOf(status);
    }

    @ParameterType("yes|no|true|false")
    public boolean bool(String booleanValue) {
        return BooleanUtils.toBoolean(booleanValue);
    }
}
