package org.springframework.samples.petclinic.owner.rest;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.util.StringUtils;

public record OwnerFilter(String firstNameContains, String lastNameContains, String city) {
    public Specification<Owner> toSpecification() {
        return Specification.where(firstNameContainsSpec())
                .and(lastNameContainsSpec())
                .and(citySpec());
    }

    private Specification<Owner> firstNameContainsSpec() {
        return ((root, query, cb) -> StringUtils.hasText(firstNameContains)
                ? cb.like(cb.lower(root.get("firstName")), "%" + firstNameContains.toLowerCase() + "%")
                : null);
    }

    private Specification<Owner> lastNameContainsSpec() {
        return ((root, query, cb) -> StringUtils.hasText(lastNameContains)
                ? cb.like(cb.lower(root.get("lastName")), "%" + lastNameContains.toLowerCase() + "%")
                : null);
    }

    private Specification<Owner> citySpec() {
        return ((root, query, cb) -> StringUtils.hasText(city)
                ? cb.equal(root.get("city"), city)
                : null);
    }
}
