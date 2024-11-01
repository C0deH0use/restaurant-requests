package pl.codehouse.restaurant.shelf;

import java.time.LocalDateTime;

class ShelfBuilder {
    private ShelfBuilder() {}

    private int id = 1001;
    private String itemName = "item";
    private int menuItemId = 10001;
    private int itemsRemaining;
    private long version;
    LocalDateTime updatedAt;

    static ShelfBuilder aShelf() {
        return new ShelfBuilder();
    }

    ShelfBuilder newShelfEntity() {
        this.id = -1;
        this.itemsRemaining = 5;
        this.version = 1;
        return this;
    }

    ShelfBuilder aShelfWithAvailableMenuItems() {
        this.itemsRemaining = 5;
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

    ShelfBuilder withItemsRemaining(Integer itemsRemaining) {
        this.itemsRemaining = itemsRemaining;
        return this;
    }

    ShelfBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    ShelfEntity build() {
        return new ShelfEntity(id, itemName, menuItemId, itemsRemaining, version, updatedAt);
    }
}
