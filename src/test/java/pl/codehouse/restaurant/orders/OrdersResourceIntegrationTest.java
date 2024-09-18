package pl.codehouse.restaurant.orders;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@Import(TestcontainersConfiguration.class)
public class OrdersResourceIntegrationTest {
    private static final int MENU_ITEM_1 = 10001;
    private static final int MENU_ITEM_2 = 10002;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @LocalServerPort
    private int serviceLocalPort;

    @BeforeEach
    void setUp() {
        RestAssured.port = serviceLocalPort;
        menuItemRepository.saveAll(List.of(
                new MenuItemEntity(MENU_ITEM_1, "Item 1", 1020, 1, false),
                new MenuItemEntity(MENU_ITEM_2, "Item 2", 1650, 1, false)))
                .collectList()
                .block();
    }

    @Test
    @DisplayName("should create order successfully")
    void should_create_order_when_request_is_valid() {
        // Given
        Map<String, Object> payload = Map.of("customerId", "1010", "menuItems", List.of(MENU_ITEM_1, MENU_ITEM_2));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/orders")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("customerId", equalTo("customerId1"))
                .body("menuItems.size()", is(2));
    }
}