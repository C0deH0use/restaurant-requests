package pl.codehouse.restaurant.orders;

import org.springframework.data.relational.core.mapping.Table;

@Table("menu_item")
record MenuItemEntity(
        int id,
        String name,
        long price,
        int volume,
        boolean packing
) {
}
