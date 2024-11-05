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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.KafkaContainer;
import pl.codehouse.restaurant.TestcontainersConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.module.webtestclient.RestAssuredWebTestClient.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.MENU_ITEM_1_ID;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.MENU_ITEM_1_NAME;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.MENU_ITEM_2_NAME;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.MENU_ITEM_3_ID;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.MENU_ITEM_3_NAME;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.aMenuItemEntity;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.aMenuItemEntityThree;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.aMenuItemEntityTwo;
import static pl.codehouse.restaurant.request.RequestEntityBuilder.REQUEST_ID;
import static pl.codehouse.restaurant.request.RequestEntityBuilder.aRequestEntity;
import static pl.codehouse.restaurant.request.RequestMenuItemEntityBuilder.REQUEST_MENU_ITEM_1_ID;
import static pl.codehouse.restaurant.request.RequestMenuItemEntityBuilder.aRequestMenuItemEntityOne;
import static pl.codehouse.restaurant.request.RequestMenuItemEntityBuilder.aRequestMenuItemEntityTwo;
import static pl.codehouse.restaurant.request.RequestMenuItemEntityBuilder.aRequestMenuItems;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@Import(TestcontainersConfiguration.class)
class RequestResourceIntegrationTest {
    private static final int REQUEST_2_ID = REQUEST_ID + 10;
    private static final int REQUEST_2_MENU_ITEM_1_ID = REQUEST_MENU_ITEM_1_ID + 10;
    private static final int REQUEST_2_MENU_ITEM_2_ID = REQUEST_MENU_ITEM_1_ID + 11;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    private Consumer<Integer, String> consumerServiceTest;

    @BeforeEach
    void setUp(
            @Autowired R2dbcEntityTemplate entityTemplate,
            @Autowired Flyway flyway,
            @Autowired KafkaContainer kafkaContainer
    ) {
        RestAssuredWebTestClient.webTestClient(webTestClient);

        flyway.clean();
        flyway.migrate();
        Flux.just(
                        aMenuItemEntity().build(),
                        aMenuItemEntityTwo().build(),
                        aMenuItemEntityThree().build(),

                        aRequestEntity().build(),
                        aRequestEntity(REQUEST_2_ID).withStatus(RequestStatus.NEW).build(),

                        // Request 1: Menu Items - One & Two -> NEW
                        aRequestMenuItemEntityOne().build(),
                        aRequestMenuItemEntityTwo().withQuantity(2).build(),


                        // Request 2: Menu Items - 3 & 4 -> READY TO COLLECT
                        aRequestMenuItems()
                                .withId(REQUEST_2_MENU_ITEM_1_ID)
                                .withRequestId(REQUEST_2_ID)
                                .withMenuId(MENU_ITEM_3_ID)
                                .withPrepared(2)
                                .withQuantity(2)
                                .build(),
                        aRequestMenuItems()
                                .withId(REQUEST_2_MENU_ITEM_2_ID)
                                .withRequestId(REQUEST_2_ID)
                                .withMenuId(MENU_ITEM_1_ID)
                                .withPrepared(2)
                                .withQuantity(2)
                                .build()

                )
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
                        Map.of("menuId", MENU_ITEM_1_ID, "quantity", 1),
                        Map.of("menuId", MENU_ITEM_3_ID, "quantity", 2)
                ));

        // When & Then
        given()
                .accept(ContentType.JSON)
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
                .body("menuItems.menuItemName.flatten().flatten()", hasItems(MENU_ITEM_1_NAME, MENU_ITEM_3_NAME))
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
                .body("$.size()", is(3))
                .body("name.flatten()", hasItems(MENU_ITEM_1_NAME, MENU_ITEM_2_NAME, MENU_ITEM_3_NAME));
    }

    @Test
    @DisplayName("should return request statues when status events change")
    void should_returnRequestStatues_when_statusEventsChange(@Autowired R2dbcEntityTemplate entityTemplate) {
        // given
        MultiValueMap<Integer, RequestStatus> expectedRequestEvents = new LinkedMultiValueMap<>();
        expectedRequestEvents.add(REQUEST_ID, RequestStatus.NEW);
        expectedRequestEvents.add(REQUEST_ID, RequestStatus.IN_PROGRESS);
        expectedRequestEvents.add(REQUEST_2_ID, RequestStatus.READY_TO_COLLECT);

        var testResult = webTestClient
                .get()
                .uri(URI.create("/request"))
                .accept(TEXT_EVENT_STREAM)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_EVENT_STREAM_VALUE)

                // when
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(TEXT_EVENT_STREAM)
                .returnResult(new ParameterizedTypeReference<ServerSentEvent<Map<String, Object>>>() {})
                .getResponseBody();

        AtomicInteger counter = new AtomicInteger();
        // then
        StepVerifier
                .create(testResult)
                .assertNext(event -> assertServerSideEvents(event, expectedRequestEvents, counter))
                .assertNext(event -> assertServerSideEvents(event, expectedRequestEvents, counter))
//                .then(() -> performDbUpdatesOnRequest1(entityTemplate).block())
//                .then(() ->  System.out.println("....After first update....."))
//                .assertNext(event -> assertServerSideEvents(event, expectedRequestEvents, counter))
//                .then(() -> performDbUpdatesOnRequest2(entityTemplate).block())
//                .assertNext(event -> assertServerSideEvents(event, expectedRequestEvents, counter))
                .verifyComplete();

        assertThat(counter.get()).isEqualTo(2);
//        assertThat(counter.get()).isEqualTo(4);
    }

    private static void assertServerSideEvents(ServerSentEvent<Map<String, Object>> event, MultiValueMap<Integer, RequestStatus> expectedRequestEvents, AtomicInteger counter) {
        int eventRequestId = Integer.parseInt(event.id());
        Map<String, Object> eventData = event.data();
        assertThat(eventData).containsKey("requestId");

        Integer requestId = (Integer) eventData.get("requestId");
        RequestStatus requestStatus = RequestStatus.valueOf((String) eventData.get("status"));
        assertThat(eventRequestId).isEqualTo(requestId);
        List<RequestStatus> expectedStatuses = expectedRequestEvents.get(requestId);
        assertThat(requestStatus).isIn(expectedStatuses);

        expectedStatuses.removeIf(status -> requestStatus == status);
        expectedRequestEvents.put(requestId, expectedStatuses);
        counter.incrementAndGet();
    }

    private Map<String, Object> getEventPayload(ConsumerRecord<Integer, String> singleRecord) {
        try {
            return objectMapper.readValue(singleRecord.value(), new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return Assertions.fail("Unable to read Consumer Record and convert to Map<String,Object>", e);
        }
    }

    private Mono<Void> performDbUpdatesOnRequest1(R2dbcEntityTemplate entityTemplate) {
        return Flux.just(
                        // Request 1: Menu Items - One & Two -> NEW
                        aRequestMenuItemEntityOne().withPrepared(1).build(),
                        aRequestMenuItemEntityTwo().withQuantity(2).withPrepared(1).build(),

                        aRequestEntity().withStatus(RequestStatus.IN_PROGRESS).build()
                )
                .doOnEach(sign -> System.out.println("Updating object: " + sign.get()))
                .flatMap(entityTemplate::update)
                .then();
    }

    private Mono<Void> performDbUpdatesOnRequest2(R2dbcEntityTemplate entityTemplate) {
        return Flux.just(
                        // Request 2: Menu Items - 3 & 4 -> READY TO COLLECT
                        aRequestMenuItems()
                                .withId(REQUEST_2_MENU_ITEM_1_ID)
                                .withRequestId(REQUEST_2_ID)
                                .withMenuId(MENU_ITEM_3_ID)
                                .withPrepared(2)
                                .withQuantity(2)
                                .build(),
                        aRequestMenuItems()
                                .withId(REQUEST_2_MENU_ITEM_2_ID)
                                .withRequestId(REQUEST_2_ID)
                                .withMenuId(MENU_ITEM_1_ID)
                                .withPrepared(2)
                                .withQuantity(2)
                                .build()
                )
                .flatMap(entityTemplate::update)
                .then();
    }
}
