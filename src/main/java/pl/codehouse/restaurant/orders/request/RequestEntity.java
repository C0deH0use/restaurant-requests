package pl.codehouse.restaurant.orders.request;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Min;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("request")
record RequestEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE) int id,
        @Min(1) int customerId,
        RequestStatus status) {

    static RequestEntity newRequestFor(int customerId) {
        return new RequestEntity(0, customerId, RequestStatus.NEW);
    }
}
