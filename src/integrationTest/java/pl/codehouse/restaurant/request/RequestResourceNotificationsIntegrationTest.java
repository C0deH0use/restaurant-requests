package pl.codehouse.restaurant.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.KafkaContainer;
import pl.codehouse.restaurant.TestcontainersConfiguration;
import pl.codehouse.restaurant.shelf.PackingStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.MENU_ITEM_3_ID;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.MENU_ITEM_4_ID;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.aMenuItemEntity;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.aMenuItemEntityFour;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.aMenuItemEntityThree;
import static pl.codehouse.restaurant.request.MenuItemEntityBuilder.aMenuItemEntityTwo;
import static pl.codehouse.restaurant.request.RequestEntityBuilder.REQUEST_ID;
import static pl.codehouse.restaurant.request.RequestEntityBuilder.aRequestEntity;
import static pl.codehouse.restaurant.request.RequestMenuItemEntityBuilder.REQUEST_MENU_ITEM_1_ID;
import static pl.codehouse.restaurant.request.RequestMenuItemEntityBuilder.REQUEST_MENU_ITEM_2_ID;
import static pl.codehouse.restaurant.request.RequestMenuItemEntityBuilder.aRequestMenuItemEntityOne;
import static pl.codehouse.restaurant.request.RequestMenuItemEntityBuilder.aRequestMenuItemEntityTwo;
import static pl.codehouse.restaurant.request.RequestMenuItemEntityBuilder.aRequestMenuItems;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "PT10S")
@ExtendWith(SpringExtension.class)
@Import(TestcontainersConfiguration.class)
class RequestResourceNotificationsIntegrationTest {

    private static final int REQUEST_2_ID = REQUEST_ID + 10;
    private static final int REQUEST_2_MENU_ITEM_1_ID = REQUEST_MENU_ITEM_1_ID + 10;
    private static final int REQUEST_2_MENU_ITEM_2_ID = REQUEST_MENU_ITEM_2_ID + 11;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.kafka.request-status.topic.topic-name}")
    private String requestStatusTopicName;

    private Producer<Integer, String> producerServiceTest;

    @BeforeEach
    void setUp(
            @Autowired R2dbcEntityTemplate entityTemplate,
            @Autowired Flyway flyway,
            @Autowired KafkaContainer kafkaContainer
    ) {
        RestAssuredWebTestClient.webTestClient(webTestClient);

        Map<String, Object> testProducerProps = KafkaTestUtils.producerProps(kafkaContainer.getBootstrapServers());
        producerServiceTest = new DefaultKafkaProducerFactory<Integer, String>(testProducerProps).createProducer();


        flyway.clean();
        flyway.migrate();

        // Insert menu items
        Flux.just(
                        aMenuItemEntity().build(),
                        aMenuItemEntityTwo().build(),
                        aMenuItemEntityThree().build(),
                        aMenuItemEntityFour().build()
                )
                .doOnNext(r -> System.out.println("Insert MenuItem: " + r))
                .flatMap(entityTemplate::insert)
                .collectList()
                .block();

        // Insert requests
        Flux.just(
                        aRequestEntity().build(),
                        aRequestEntity(REQUEST_2_ID).withStatus(RequestStatus.NEW).build()
                )
                .doOnNext(r -> System.out.println("Insert Request: " + r))
                .flatMap(entityTemplate::insert)
                .collectList()
                .block();

        // Insert request menu items
        Flux.just(
                        aRequestMenuItemEntityOne().build(),
                        aRequestMenuItemEntityTwo().withQuantity(2).build()
                )
                .doOnNext(r -> System.out.println("Insert Request MenuItem: " + r))
                .flatMap(entityTemplate::insert)
                .collectList()
                .block();

        sendStatusUpdateFor(REQUEST_ID, RequestStatus.IN_PROGRESS, PackingStatus.IN_PROGRESS);
        sendStatusUpdateFor(REQUEST_2_ID, RequestStatus.IN_PROGRESS, PackingStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("should return request statues when status events change")
    void should_returnRequestStatues_when_statusEventsChange(@Autowired R2dbcEntityTemplate entityTemplate) {
        // given
        MultiValueMap<Integer, RequestStatus> expectedRequestEvents = new LinkedMultiValueMap<>();
        expectedRequestEvents.add(REQUEST_ID, RequestStatus.IN_PROGRESS);
        expectedRequestEvents.add(REQUEST_ID, RequestStatus.READY_TO_COLLECT);
        expectedRequestEvents.add(REQUEST_2_ID, RequestStatus.IN_PROGRESS);
        expectedRequestEvents.add(REQUEST_2_ID, RequestStatus.READY_TO_COLLECT);

        var testResult = webTestClient
                .get()
                .uri(URI.create("/request/notification/status"))
                .accept(TEXT_EVENT_STREAM)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

                // when
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(TEXT_EVENT_STREAM)
                .returnResult(new ParameterizedTypeReference<ServerSentEvent<Map<String, Object>>>() {
                })
                .getResponseBody();

        AtomicInteger counter = new AtomicInteger();
        // then
        StepVerifier
                .create(testResult)
                .assertNext(event -> assertServerSideEvents(event, expectedRequestEvents, counter))
                .assertNext(event -> assertServerSideEvents(event, expectedRequestEvents, counter))
                .then(() -> performDbUpdatesOnRequest1(entityTemplate).block())
                .then(() ->  System.out.println("....After first update....."))
                .assertNext(event -> assertServerSideEvents(event, expectedRequestEvents, counter))
                .then(() -> performDbUpdatesOnRequest2(entityTemplate).block())
                .assertNext(event -> assertServerSideEvents(event, expectedRequestEvents, counter))
                .verifyTimeout(Duration.ofSeconds(3));

        assertThat(counter.get()).isEqualTo(4);
        assertThat(expectedRequestEvents.get(REQUEST_ID)).isEmpty();
        assertThat(expectedRequestEvents.get(REQUEST_2_ID)).isEmpty();
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

    private Mono<Void> performDbUpdatesOnRequest1(R2dbcEntityTemplate entityTemplate) {
        return Flux.just(
                        // Request 1: Menu Items - One & Two -> NEW
                        aRequestMenuItemEntityOne().withPrepared(1).build(),
                        aRequestMenuItemEntityTwo().withQuantity(2).withPrepared(1).build()
                )
                .doOnEach(sign -> System.out.println("Updating object: " + sign.get()))
                .flatMap(entityTemplate::update)
                .then(requestRepository.updateStatusById(REQUEST_ID, RequestStatus.READY_TO_COLLECT))
                .doFinally((x) -> sendStatusUpdateFor(REQUEST_ID, RequestStatus.READY_TO_COLLECT, PackingStatus.READY_TO_COLLECT))
                .then();
    }

    private Mono<Void> performDbUpdatesOnRequest2(R2dbcEntityTemplate entityTemplate) {
        return Flux.just(
                        // Request 2: Menu Items - 3 & 4 -> READY TO COLLECT
                        aRequestMenuItemEntityTwo()
                                .withId(REQUEST_2_MENU_ITEM_1_ID)
                                .withRequestId(REQUEST_2_ID)
                                .withMenuId(MENU_ITEM_3_ID)
                                .withPrepared(2)
                                .withQuantity(2)
                                .build(),
                        aRequestMenuItems()
                                .withId(REQUEST_2_MENU_ITEM_2_ID)
                                .withRequestId(REQUEST_2_ID)
                                .withMenuId(MENU_ITEM_4_ID)
                                .withPrepared(2)
                                .withQuantity(2)
                                .build()
                )
                .doOnEach(sign -> System.out.println("Inserting object: " + sign.get()))
                .flatMap(entityTemplate::insert)
                .then(requestRepository.updateStatusById(REQUEST_2_ID, RequestStatus.READY_TO_COLLECT))
                .doFinally((x) -> sendStatusUpdateFor(REQUEST_2_ID, RequestStatus.READY_TO_COLLECT, PackingStatus.READY_TO_COLLECT))
                .then();
    }

    private String createPayload(int requestId, RequestStatus requestStatus, PackingStatus packingStatus) {
        Map<String, Object> payload = Map.of(
                "requestId", requestId,
                "requestStatus", requestStatus,
                "packingStatus", packingStatus
        );
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return Assertions.fail("Failed to create payload", e);
        }
    }

    private static @NotNull RecordHeaders getRecordHeaders() {
        RecordHeaders recordHeaders = new RecordHeaders();
        recordHeaders.add("__TypeId__", "pl.codehouse.restaurant.request.RequestStatusChangeMessage".getBytes(StandardCharsets.UTF_8));
        return recordHeaders;
    }

    private void sendStatusUpdateFor(int requestId, RequestStatus requestStatus, PackingStatus packingStatus) {
        var producerRecord = new ProducerRecord<Integer, String>(
                requestStatusTopicName,
                null,
                null,
                requestId,
                createPayload(requestId, requestStatus, packingStatus),
                getRecordHeaders()
        );
        producerServiceTest.send(producerRecord);
    }

}
