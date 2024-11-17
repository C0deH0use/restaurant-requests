package pl.codehouse.restaurant.request;

/**
 * Enum representing the various statuses a request can have in the restaurant system.
 * This enum is used to track the lifecycle of a request from creation to completion.
 */
public enum RequestStatus {
    /**
     * Indicates a newly created request that hasn't been processed yet.
     */
    NEW,

    /**
     * Indicates a request that is currently being processed or prepared.
     */
    IN_PROGRESS,

    /**
     * Indicates a request that has been fully prepared and is ready for the customer to collect.
     */
    READY_TO_COLLECT,

    /**
     * Indicates a request that has been collected by the customer, completing the order process.
     */
    COLLECTED
}
