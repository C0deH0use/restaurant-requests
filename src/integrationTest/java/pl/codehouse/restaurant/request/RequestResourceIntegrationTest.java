package pl.codehouse.restaurant.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;
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
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.KafkaContainer;
import pl.codehouse.restaurant.TestcontainersConfiguration;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.restassured.module.webtestclient.RestAssuredWebTestClient.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@Import(TestcontainersConfiguration.class)
class RequestResourceIntegrationTest {
    private static final int MENU_ITEM_1 = 101;
    private static final int MENU_ITEM_2 = 102;

    @Autowired
    private R2dbcEntityTemplate entityTemplate;

    @Autowired
    private Flyway flyway;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private KafkaContainer kafkaContainer;

    @Autowired
    private ObjectMapper objectMapper;

    private Consumer<Integer, String> consumerServiceTest;

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

        Map<String, Object> testConsumerProps = KafkaTestUtils.consumerProps(
                kafkaContainer.getBootstrapServers(),
                "test-request_worker__clientId",
                "false"
        );
        consumerServiceTest = new DefaultKafkaConsumerFactory<Integer, String>(testConsumerProps)
                .createConsumer("request_worker__clientId");
        consumerServiceTest.subscribe(Collections.singletonList("shelf-events"));
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

        ConsumerRecord<Integer, String> singleRecord = KafkaTestUtils.getSingleRecord(consumerServiceTest, "shelf-events");

        assertThat(singleRecord.value()).isNotNull();
        Map<String, Object> eventPayload = getEventPayload(singleRecord);
        assertThat(eventPayload)
                .containsKey("requestId")
                .containsEntry("eventType", "NEW_REQUEST")
                .containsEntry("menuItemId", -1)
                .containsEntry("quantity", -1);
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

    private Map<String, Object> getEventPayload(ConsumerRecord<Integer, String> singleRecord) {
        try {
            return objectMapper.readValue(singleRecord.value(), new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return Assertions.fail("Unable to read Consumer Record and convert to Map<String,Object>", e);
        }
    }
}
