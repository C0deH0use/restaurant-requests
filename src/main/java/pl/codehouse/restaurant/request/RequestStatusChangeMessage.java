package pl.codehouse.restaurant.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.codehouse.restaurant.shelf.PackingStatus;

/**
 * Represents a message for notifying changes in the status of a request.
 * This record is used for serialization when publishing status change events to Kafka.
 */
public record RequestStatusChangeMessage(
        @JsonProperty("requestId")
        int requestId,
        @JsonProperty("requestStatus")
        RequestStatus requestStatus,
        @JsonProperty("packingStatus")
        PackingStatus packingStatus
) {
}
