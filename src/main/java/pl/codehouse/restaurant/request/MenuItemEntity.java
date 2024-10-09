package pl.codehouse.restaurant.request;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("menu_item")
record MenuItemEntity(
        @Id
        @GeneratedValue(strategy= GenerationType.SEQUENCE) int id,
        @NotNull String name,
        @Min(1) long price,
        @Min(1) int volume,
        boolean packing,
        boolean immediate
) {
}
