package ru.amplicode.orders.rest.filter;

import org.springframework.data.jpa.domain.Specification;
import ru.amplicode.orders.domain.Order;
import ru.amplicode.orders.domain.OrderStatus;

public record OrderFilter(Long customerId, OrderStatus orderStatus, Long cityId) {
    public Specification<Order> toSpecification() {
        return Specification.where(customerIdSpec())
            .and(orderStatusSpec())
            .and(cityIdSpec());
    }

    private Specification<Order> customerIdSpec() {
        return ((root, query, cb) -> customerId != null
            ? cb.equal(root.get("customer").get("id"), customerId)
            : null);
    }

    private Specification<Order> orderStatusSpec() {
        return ((root, query, cb) -> orderStatus != null
            ? cb.equal(root.get("orderStatus"), orderStatus)
            : null);
    }

    private Specification<Order> cityIdSpec() {
        return ((root, query, cb) -> cityId != null
            ? cb.equal(root.get("city").get("id"), cityId)
            : null);
    }
}
