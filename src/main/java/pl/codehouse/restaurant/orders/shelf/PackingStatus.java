package pl.codehouse.restaurant.orders.shelf;

/**
 * Enum representing the various statuses of packing for a request in the restaurant system.
 * This enum is used to track the progress of packing items for a customer's order.
 */
public enum PackingStatus {
    /**
     * Indicates that all items for the request have been packed and are ready for the customer to collect.
     */
    READY_TO_COLLECT,

    /**
     * Indicates that additional items have been requested from the kitchen to complete the order.
     */
    REQUESTED_ITEMS,

    /**
     * Indicates that the packing process for the request is currently in progress.
     */
    IN_PROGRESS,

    /**
     * Indicates that the packing process for the request has not yet started.
     */
    NOT_STARTED,
}
