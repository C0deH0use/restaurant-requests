package pl.codehouse.restaurant.orders;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table("order")
record OrderEntity(
        @Id Integer id,
        List<MenuItemEntity> menuItems,
        int customerId) {

    public OrderEntity {
        menuItems = List.copyOf(menuItems);
    }
}