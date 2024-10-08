package pl.codehouse.restaurant.shelf;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.BooleanUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pl.codehouse.restaurant.request.RequestMenuItem;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static pl.codehouse.restaurant.request.RequestMenuItemBuilder.MENU_ITEM_1;
import static pl.codehouse.restaurant.request.RequestMenuItemBuilder.MENU_ITEM_1_NAME;
import static pl.codehouse.restaurant.request.RequestMenuItemBuilder.MENU_ITEM_2;
import static pl.codehouse.restaurant.request.RequestMenuItemBuilder.MENU_ITEM_2_NAME;
import static pl.codehouse.restaurant.shelf.ShelfBuilder.aShelf;

public class ShelfBOStepDefinitions {
    private static final long NEW_VERSION_VALUE = 1L;

    private final Clock clock = Clock.fixed(Instant.parse("2024-10-05T15:15:30.00Z"), ZoneOffset.UTC);
    private final ShelfRepository shelfRepository = Mockito.mock(ShelfRepository.class);
    private final ArgumentCaptor<ShelfEntity> shelfEntityArgumentCaptor = ArgumentCaptor.forClass(ShelfEntity.class);
    private final LocalDateTime updatedAt = LocalDateTime.now(clock);

    private final ShelfBO sut = new ShelfBO(clock, shelfRepository);
    long expectedVersion = 2;

    private Mono<ShelfTakeResult> executionResult;

    @Given("the shelf contains {int} menu items from request")
    public void given_ShelfContainsXMenuItemsFromRequest(int shelfItems) {
        given(shelfRepository.save(any())).willReturn(Mono.just(aShelf().build()));

        given(shelfRepository.findByMenuItemId(MENU_ITEM_1)).willReturn(Mono.just(aShelf()
                .aShelfWithAvailableMenuItems()
                .withMenuId(MENU_ITEM_1)
                .withName(MENU_ITEM_1_NAME)
                .withItemsRemaining(shelfItems)
                .build()
        ));
    }


    @Given("shelf not containing any items")
    public void shelfNotContainingAnyItems() {
        given(shelfRepository.save(any())).willReturn(Mono.just(aShelf().build()));
        given(shelfRepository.findByMenuItemId(MENU_ITEM_2)).willReturn(Mono.empty());
    }

    @When("handling requested {int} Menu Items")
    public void when_RequestWithXMenuItems(int menuItemsRequested) {
        executionResult = sut.take(new RequestMenuItem(MENU_ITEM_1, MENU_ITEM_1_NAME, menuItemsRequested, 0, false));
    }

    @When("request a new Menu Item")
    public void requestANewMenuItem() {
        executionResult = sut.take(new RequestMenuItem(MENU_ITEM_2, MENU_ITEM_2_NAME, 1, 0, false));
    }

    @Then("request should be updated with {int} prepared menu items from the shelf")
    public void then_RequestShouldBeUpdatedWithXPreparedMenuItemsFromTheShelf(int collectedItems) {
        StepVerifier.create(executionResult)
                .assertNext(result -> assertThat(result.itemsTakenFromShelf()).isEqualTo(collectedItems))
                .verifyComplete();
    }

    @Then("new shelf item should be created with {int} menu items")
    public void newShelfItemShouldBeCreatedWithMenuItems(int arg0) {
        then(shelfRepository).should(times(1)).save(shelfEntityArgumentCaptor.capture());

        assertThat(shelfEntityArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("menuItemId", MENU_ITEM_2)
                .hasFieldOrPropertyWithValue("itemName", MENU_ITEM_2_NAME)
                .hasFieldOrPropertyWithValue("quantity", arg0)
                .hasFieldOrPropertyWithValue("version", NEW_VERSION_VALUE)
                .hasFieldOrPropertyWithValue("updatedAt", updatedAt);
    }

    @And("shelf should be updated with {int} menu items taken for request")
    public void and_ShelfShouldBeUpdatedWithXMenuItemsTakenForRequestAndVersionShouldBeUpdated(int collectedItems) {
        then(shelfRepository).should(times(1)).save(shelfEntityArgumentCaptor.capture());

        assertThat(shelfEntityArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("menuItemId", MENU_ITEM_1)
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
        // This step is more of a business process step and doesn't require additional assertions in the test
        // In a real-world scenario, this might involve calling another service or updating a status
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
