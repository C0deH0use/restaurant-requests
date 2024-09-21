package pl.codehouse.restaurant.request;

import org.springframework.data.relational.core.mapping.Table;

@Table("request_menu_item")
public record RequestMenuItemEntity(
        int requestId,
        int menuItemId
) {
}
