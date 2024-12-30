package pl.codehouse.restaurant.orders.shelf;

import java.time.LocalDateTime;

class ShelfBuilder {
    private ShelfBuilder() {}

    private int id = 1001;
    private String itemName = "item";
    private int menuItemId = 10001;
    private int itemsQuantity;
    private long version;
    LocalDateTime updatedAt;

    static ShelfBuilder aShelf() {
        return new ShelfBuilder();
    }

    ShelfBuilder newShelfEntity() {
        this.id = 0;
        this.itemsQuantity = 0;
        this.version = 0;
        return this;
    }

    ShelfBuilder aShelfWithAvailableMenuItems() {
        this.itemsQuantity = 5;
        this.version = 1;
        return this;
    }

    ShelfBuilder withMenuId(Integer menuItemId) {
        this.menuItemId = menuItemId;
        return this;
    }

    ShelfBuilder withName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    ShelfBuilder withItemsQuantity(Integer itemsQuantity) {
        this.itemsQuantity = itemsQuantity;
        return this;
    }

    ShelfBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    ShelfEntity build() {
        return new ShelfEntity(id, itemName, menuItemId, itemsQuantity, version, updatedAt);
    }
}
