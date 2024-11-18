package pl.codehouse.restaurant.exceptions;

/**
 * Enum representing different types of resources in the restaurant system.
 * This enum is used to categorize various entities for exception handling and resource management.
 */
public enum ResourceType {
    /**
     * Represents a menu item in the restaurant.
     */
    MENU_ITEM,

    /**
     * Represents an order placed by a customer.
     */
    ORDER,

    /**
     * Represents a customer of the restaurant.
     */
    CUSTOMER,

    /**
     * Represents an item on the shelf, typically used for inventory management.
     */
    SHELF_ITEM,
}
