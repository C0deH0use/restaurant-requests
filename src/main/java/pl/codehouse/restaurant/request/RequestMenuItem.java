package pl.codehouse.restaurant.request;

public record RequestMenuItem(
        int menuItemId,
        String menuItemName,
        int quantity,
        int prepared,
        boolean immediatePreparation
) {

    public static RequestMenuItem from(int menuItemId, String menuItemName, int quantity, int prepared, boolean immediatePreparation) {
        return new RequestMenuItem(menuItemId, menuItemName, quantity, prepared, immediatePreparation);
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
