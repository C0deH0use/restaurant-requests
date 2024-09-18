package pl.codehouse.restaurant.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceNotFoundException extends ResponseStatusException {
    private final ResourceType resourceType;

    public ResourceNotFoundException(String message, ResourceType resourceType) {
        super(HttpStatus.NOT_FOUND, message);
        this.resourceType = resourceType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }
}
