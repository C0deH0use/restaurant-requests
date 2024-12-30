package pl.codehouse.restaurant.orders.request;

public class RequestEntityBuilder {
    public static final int REQUEST_ID = 1111;
    public static final int CUSTOMER_ID = 1201;

    private int id = REQUEST_ID;
    private RequestStatus status = RequestStatus.IN_PROGRESS;
    private int customerId = CUSTOMER_ID;

    private RequestEntityBuilder() {
    }

    public static RequestEntityBuilder aRequestEntity() {
        return new RequestEntityBuilder();
    }

    public static RequestEntityBuilder aRequestEntity(int requestId) {
        return new RequestEntityBuilder()
                .withId(requestId);
    }

    public RequestEntityBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public RequestEntityBuilder withCustomerId(int customerId) {
        this.customerId = customerId;
        return this;
    }

    public RequestEntityBuilder withStatus(RequestStatus status) {
        this.status = status;
        return this;
    }

    public RequestEntity build() {
        return new RequestEntity(id, customerId, status);
    }
}
