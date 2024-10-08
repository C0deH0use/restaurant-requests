package pl.codehouse.restaurant.request;

import io.restassured.http.ContentType;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.codehouse.restaurant.TestcontainersConfiguration;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static io.restassured.module.webtestclient.RestAssuredWebTestClient.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.flyway.cleanDisabled=false")
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@Import(TestcontainersConfiguration.class)
public class RequestResourceIntegrationTest {
    private static final int MENU_ITEM_1 = 101;
    private static final int MENU_ITEM_2 = 102;

    @Autowired
    private R2dbcEntityTemplate entityTemplate;

    @Autowired
    private Flyway flyway;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        RestAssuredWebTestClient.webTestClient(webTestClient);

        flyway.clean();
        flyway.migrate();
        Flux.just(
                        new MenuItemEntity(MENU_ITEM_1, "Item 1", 1020, 1, false, false),
                        new MenuItemEntity(MENU_ITEM_2, "Item 2", 1650, 1, false, false))
                .flatMap(o -> {
                    System.out.println("Inserting object: " + o.toString());
                    return entityTemplate.insert(o);
                })
                .collectList()
                .block();
    }

    @Test
    @DisplayName("should create request successfully")
    void should_create_request_when_request_is_valid() {
        // Given
        Map<String, Object> payload = Map.of(
                "customerId", "1010",
                "menuItems", List.of(
                        Map.of("menuId", MENU_ITEM_1, "quantity", 1),
                        Map.of("menuId", MENU_ITEM_2, "quantity", 2)
                ));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(payload)

                .log().all(true)
                .when()
                .post("/request")

                .then()
                .log().all(true)
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("requestId", equalTo(1000))
                .body("customerId", equalTo(1010))
                .body("menuItems.size()", is(2))
                .body("menuItems[0]", hasKey("menuItemId"))
                .body("menuItems[0]", hasKey("quantity"))
                .body("menuItems[0]", hasKey("prepared"))
                .body("menuItems[0]", hasKey("finished"))
                .body("menuItems[0]", hasKey("immediatePreparation"))
                .body("menuItems.menuItemName.flatten().flatten()", hasItems("Item 1", "Item 2"))
        ;
    }

    @Test
    @DisplayName("should return available menu items")
    void should_return_available_menu_items() {
        // When & Then
        given()
                .contentType(ContentType.JSON)

                .when()
                .get("/request/menu-items")

                .then()
                .log().all(true)
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$.size()", is(2))
                .body("name.flatten()", hasItems("Item 1", "Item 2"));
    }
}