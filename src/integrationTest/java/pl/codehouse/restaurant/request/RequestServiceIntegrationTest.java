package pl.codehouse.restaurant.request;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.codehouse.restaurant.TestcontainersConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.MENU_ITEM_2_ID;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.aMenuItemEntityFour;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.aMenuItemEntityOne;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.aMenuItemEntityThree;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.aMenuItemEntityTwo;
import static pl.codehouse.restaurant.request.RequestEntityBuilder.CUSTOMER_ID;
import static pl.codehouse.restaurant.request.RequestEntityBuilder.REQUEST_ID;
import static pl.codehouse.restaurant.request.RequestEntityBuilder.aRequestEntity;
import static pl.codehouse.restaurant.request.RequestMenuItemBuilder.aRequestMenuItemOne;
import static pl.codehouse.restaurant.request.RequestMenuItemBuilder.aRequestMenuItemTwo;
import static pl.codehouse.restaurant.request.RequestMenuItemEntityBuilder.aRequestMenuItemEntityOne;
import static pl.codehouse.restaurant.request.RequestMenuItemEntityBuilder.aRequestMenuItemEntityTwo;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@Import(TestcontainersConfiguration.class)
class RequestServiceIntegrationTest {
    @Autowired
    private Flyway flyway;

    @Autowired
    private R2dbcEntityTemplate entityTemplate;

    @Autowired
    private RequestService sut;

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
        Flux.just(
                        // Menu item entities
                        aMenuItemEntityOne().build(),
                        aMenuItemEntityTwo().build(),
                        aMenuItemEntityThree().build(),
                        aMenuItemEntityFour().build(),

                        // request entity One
                        aRequestEntity().build(),

                        // Request Menu Items - One & Two
                        aRequestMenuItemEntityOne().build(),
                        aRequestMenuItemEntityTwo().withQuantity(2).build()
                )
                .flatMap(o -> {
                    System.out.println("Inserting object: " + o.toString());
                    return entityTemplate.insert(o);
                })
                .collectList()
                .block();
    }

    @Test
    @DisplayName("should fetch stored request")
    void shouldFetchStoredRequest() {
        // when
        Mono<RequestDto> response = sut.findById(REQUEST_ID);

        // then
        StepVerifier.create(response)
                .assertNext(result -> {
                    assertThat(result)
                            .hasFieldOrPropertyWithValue("customerId", CUSTOMER_ID)
                            .hasFieldOrPropertyWithValue("preparedItemsCount", 0)
                            .hasFieldOrPropertyWithValue("totalItemsCount", 3);
                    assertThat(result.menuItems())
                            .hasSize(2)
                            .contains(
                                    aRequestMenuItemOne().build(),
                                    aRequestMenuItemTwo().withQuantity(2).build());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("should update request with packed values for a given Request Menu Item")
    void shouldUpdateRequestWithPackedValuesForAGivenMenuItem() {
        // given
        UpdatePreparedMenuItemsDto updateDto = new UpdatePreparedMenuItemsDto(REQUEST_ID, MENU_ITEM_2_ID, 2);

        // when
        Mono<RequestDto> response = sut.updateCollectedItems(updateDto);

        // then
        StepVerifier.create(response)
                .assertNext(result -> {
                    assertThat(result)
                            .hasFieldOrPropertyWithValue("preparedItemsCount", 2)
                            .hasFieldOrPropertyWithValue("totalItemsCount", 3);
                    assertThat(result.menuItems())
                            .hasSize(2)
                            .contains(
                                    aRequestMenuItemOne().build(),
                                    aRequestMenuItemTwo()
                                            .withQuantity(2)
                                            .withPrepared(2)
                                            .build());
                })
                .verifyComplete();
    }
}
