package pl.codehouse.restaurant.shelf;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("shelf")
record ShelfEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE) int id,
        String itemName,
        int menuItemId,
        int quantity,
        long version,
        LocalDateTime updatedAt
) {

    ShelfEntity withQuantityUpdate(int newQuantity, LocalDateTime updatedAt) {
        long newVersion = version + 1;
        return new ShelfEntity(id, itemName, menuItemId, newQuantity, newVersion, updatedAt);
    }
}
