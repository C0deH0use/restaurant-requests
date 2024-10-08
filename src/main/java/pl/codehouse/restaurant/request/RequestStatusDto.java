package pl.codehouse.restaurant.request;

import pl.codehouse.restaurant.shelf.PackingStatus;

public record RequestStatusDto(
        int requestId,
        PackingStatus status,
        int preparedItems,
        int totalItems
) {
}
