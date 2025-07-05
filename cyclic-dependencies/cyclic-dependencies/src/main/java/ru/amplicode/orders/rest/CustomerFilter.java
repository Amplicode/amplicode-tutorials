package ru.amplicode.orders.rest;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import ru.amplicode.orders.domain.Customer;

public record CustomerFilter(String nameLike, String emailLike) {
    public Specification<Customer> toSpecification() {
        return Specification.where(nameLikeSpec())
            .and(emailLikeSpec());
    }

    private Specification<Customer> nameLikeSpec() {
        return ((root, query, cb) -> StringUtils.hasText(nameLike)
            ? cb.like(cb.lower(root.get("name")), nameLike.toLowerCase())
            : null);
    }

    private Specification<Customer> emailLikeSpec() {
        return ((root, query, cb) -> StringUtils.hasText(emailLike)
            ? cb.like(cb.lower(root.get("email")), emailLike.toLowerCase())
            : null);
    }
}
