package pl.codehouse.restaurant.request;

import static pl.codehouse.restaurant.request.RequestDtoBuilder.CUSTOMER_ID;
import static pl.codehouse.restaurant.request.RequestDtoBuilder.REQUEST_ID;

public class RequestEntityBuilder {
    private int id = REQUEST_ID;
    private int customerId = CUSTOMER_ID;

    private RequestEntityBuilder() {
    }

    public static RequestEntityBuilder aRequestEntity() {
        return new RequestEntityBuilder();
    }

    public RequestEntityBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public RequestEntityBuilder withCustomerId(int customerId) {
        this.customerId = customerId;
        return this;
    }

    public RequestEntity build() {
        return new RequestEntity(id, customerId);
    }
}
