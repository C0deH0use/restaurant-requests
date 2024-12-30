package pl.codehouse.restaurant.orders.request;

import java.util.List;
import java.util.function.Function;

/**
 * Represents a data transfer object for a restaurant request.
 * This record encapsulates all the necessary information about a customer's order.
 */
public record RequestDto(
        int requestId,
        int customerId,
        List<RequestMenuItem> menuItems,
        int preparedItemsCount,
        int totalItemsCount,
        RequestStatus status
) {
    /**
     * Creates a RequestDto from the given entities.
     *
     * @param savedEntity The saved request entity.
     * @param requestMenuItems List of request menu item entities.
     * @param menuItemEntities List of menu item entities.
     * @return A new RequestDto instance.
     */
    public static RequestDto from(RequestEntity savedEntity, List<RequestMenuItemEntity> requestMenuItems, List<MenuItemEntity> menuItemEntities) {
        var menuItemList = requestMenuItems.stream()
                .map(mapRequestMenuItem(menuItemEntities))
                .toList();
        var preparedItemsCount = menuItemList.stream().mapToInt(RequestMenuItem::prepared).sum();
        var totalItemsCount = menuItemList.stream().mapToInt(RequestMenuItem::quantity).sum();
        var status = setCorrectStatus(preparedItemsCount, totalItemsCount);
        return new RequestDto(savedEntity.id(), savedEntity.customerId(), menuItemList, preparedItemsCount, totalItemsCount, status);
    }

    /**
     * Maps a RequestMenuItemEntity to a RequestMenuItem.
     *
     * @param menuItemEntities List of menu item entities to search for the corresponding menu item.
     * @return A function that performs the mapping.
     */
    private static Function<RequestMenuItemEntity, RequestMenuItem> mapRequestMenuItem(List<MenuItemEntity> menuItemEntities) {
        return item -> {
            var menuItem = menuItemEntities.stream()
                    .filter(entity -> entity.id() == item.menuItemId())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Menu item not found"));
            return RequestMenuItem.from(item.id(), menuItem.id(), menuItem.name(), item.quantity(), item.prepared(), item.immediate());
        };
    }

    /**
     * Determines the correct RequestStatus based on the number of prepared items and total items.
     *
     * @param preparedItemsCount The number of prepared items.
     * @param totalItemsCount The total number of items in the request.
     * @return The appropriate RequestStatus.
     * @throws IllegalStateException if the prepared items count exceeds the total items count.
     */
    private static RequestStatus setCorrectStatus(long preparedItemsCount, long totalItemsCount) {
        return switch (Long.compare(preparedItemsCount, totalItemsCount)) {
            case -1 -> preparedItemsCount == 0 ? RequestStatus.NEW : RequestStatus.IN_PROGRESS;
            case 0 -> RequestStatus.READY_TO_COLLECT;
            case 1 -> throw new IllegalStateException("Prepared items count cannot exceed total items count");
            default -> throw new IllegalStateException("Unexpected comparison result");
        };
    }
}
