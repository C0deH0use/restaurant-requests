package pl.codehouse.restaurant.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends ResponseStatusException {
    private final ResourceType resourceType;

    /**
     * Constructs a new ResourceNotFoundException.
     *
     * @param message The detail message.
     * @param resourceType The type of resource that was not found.
     */
    public ResourceNotFoundException(String message, ResourceType resourceType) {
        super(HttpStatus.NOT_FOUND, message);
        this.resourceType = resourceType;
    }

    /**
     * Gets the type of resource that was not found.
     *
     * @return The ResourceType of the not found resource.
     */
    public ResourceType getResourceType() {
        return resourceType;
    }
}
