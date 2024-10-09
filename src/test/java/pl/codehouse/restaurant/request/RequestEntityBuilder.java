package pl.codehouse.restaurant.request;

public class RequestEntityBuilder {
    public static final int REQUEST_ID = 1111;
    public static final int CUSTOMER_ID = 1201;

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
