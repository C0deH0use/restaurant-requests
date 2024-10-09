package pl.codehouse.restaurant.request;

public record RequestMenuItem(
        int id,
        int menuItemId,
        String menuItemName,
        int quantity,
        int prepared,
        boolean immediatePreparation
) {

    public static RequestMenuItem from(int id, int menuItemId, String menuItemName, int quantity, int prepared, boolean immediatePreparation) {
        return new RequestMenuItem(id, menuItemId, menuItemName, quantity, prepared, immediatePreparation);
    }

    public boolean notFinished() {
        return quantity != prepared;
    }

    public boolean isFinished() {
        return quantity == prepared;
    }

    public int remainingItems() {
        return quantity - prepared;
    }
}
