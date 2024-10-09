package pl.codehouse.restaurant.request;

import java.util.List;
import java.util.function.Function;

public record RequestDto(
        int requestId,
        int customerId,
        List<RequestMenuItem> menuItems,
        int preparedItemsCount,
        int totalItemsCount,
        RequestStatus status
) {
    public static RequestDto from(RequestEntity savedEntity, List<RequestMenuItemEntity> requestMenuItems, List<MenuItemEntity> menuItemEntities) {
        var menuItemList = requestMenuItems.stream()
                .map(mapRequestMenuItem(menuItemEntities))
                .toList();
        var preparedItemsCount = menuItemList.stream().mapToInt(RequestMenuItem::prepared).sum();
        var totalItemsCount = menuItemList.stream().mapToInt(RequestMenuItem::quantity).sum();
        var status = setCorrectStatus(preparedItemsCount, totalItemsCount);
        return new RequestDto(savedEntity.id(), savedEntity.customerId(), menuItemList, preparedItemsCount, totalItemsCount, status);
    }

    private static Function<RequestMenuItemEntity, RequestMenuItem> mapRequestMenuItem(List<MenuItemEntity> menuItemEntities) {
        return item -> {
            var menuItem = menuItemEntities.stream()
                    .filter(entity -> entity.id() == item.menuItemId())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Menu item not found"));
            return RequestMenuItem.from(item.id(), menuItem.id(), menuItem.name(), item.quantity(), item.prepared(), item.immediate());
        };
    }

    private static RequestStatus setCorrectStatus(long preparedItemsCount, long totalItemsCount) {
        return switch (Long.compare(preparedItemsCount, totalItemsCount)) {
            case -1 -> preparedItemsCount == 0 ? RequestStatus.NEW : RequestStatus.IN_PROGRESS;
            case 0 -> RequestStatus.READY_TO_COLLECT;
            case 1 -> throw new IllegalStateException("Prepared items count cannot exceed total items count");
            default -> throw new IllegalStateException("Unexpected comparison result");
        };
    }
}
