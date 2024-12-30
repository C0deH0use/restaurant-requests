package pl.codehouse.restaurant.orders.request;

/**
 * Enum representing different types of events in the restaurant system.
 * These events are used to track significant occurrences in the request processing workflow.
 */
public enum EventType {
    /**
     * Represents the event of a new request being created in the system.
     */
    NEW_REQUEST,

    /**
     * Represents the event of an item being added to a shelf, typically after preparation.
     */
    ITEM_ADDED_ON_SHELF,
}
