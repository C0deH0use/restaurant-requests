package pl.codehouse.restaurant.orders.request;

/**
 * Represents a menu item within a request in the restaurant system.
 * This record encapsulates all the necessary information about a specific menu item in a customer's order.
 */
public record RequestMenuItem(
        int id,
        int menuItemId,
        String menuItemName,
        int quantity,
        int prepared,
        boolean immediatePreparation
) {

    /**
     * Creates a new RequestMenuItem instance from the given parameters.
     *
     * @param id The unique identifier for this request menu item.
     * @param menuItemId The ID of the menu item.
     * @param menuItemName The name of the menu item.
     * @param quantity The total quantity ordered.
     * @param prepared The number of items already prepared.
     * @param immediatePreparation Whether this item requires immediate preparation.
     * @return A new RequestMenuItem instance.
     */
    public static RequestMenuItem from(int id, int menuItemId, String menuItemName, int quantity, int prepared, boolean immediatePreparation) {
        return new RequestMenuItem(id, menuItemId, menuItemName, quantity, prepared, immediatePreparation);
    }

    /**
     * Checks if the preparation of this menu item is not finished.
     *
     * @return true if the prepared quantity is less than the total quantity, false otherwise.
     */
    public boolean notFinished() {
        return quantity != prepared;
    }

    /**
     * Checks if the preparation of this menu item is finished.
     *
     * @return true if the prepared quantity equals the total quantity, false otherwise.
     */
    public boolean isFinished() {
        return quantity == prepared;
    }

    /**
     * Calculates the number of items remaining to be prepared.
     *
     * @return The difference between the total quantity and the prepared quantity.
     */
    public int remainingItems() {
        return quantity - prepared;
    }
}
