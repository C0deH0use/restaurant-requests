package pl.codehouse.restaurant.shelf;

/**
 * Represents the result of a take operation from the shelf in the restaurant system.
 * This record encapsulates the packing status and the number of items taken from the shelf.
 */
public record ShelfTakeResult(
        PackingStatus packingStatus,
        Integer itemsTakenFromShelf
) {
}
