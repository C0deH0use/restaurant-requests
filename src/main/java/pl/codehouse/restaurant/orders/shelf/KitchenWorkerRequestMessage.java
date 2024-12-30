package pl.codehouse.restaurant.orders.shelf;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a message for notifying changes in the status of a request.
 * This record is used for serialization when publishing status change events to Kafka.
 */
public record KitchenWorkerRequestMessage(
        @JsonProperty("menuItemId")
        int menuItemId,
        @JsonProperty("quantity")
        int quantity
) {
}
