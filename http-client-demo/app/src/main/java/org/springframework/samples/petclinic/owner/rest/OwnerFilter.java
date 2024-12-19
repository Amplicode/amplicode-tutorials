package org.springframework.samples.petclinic.owner.rest;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.util.StringUtils;

public record OwnerFilter(String firstNameStarts, String lastNameStarts, String city) {
    public Specification<Owner> toSpecification() {
        return Specification.where(firstNameStartsSpec())
                .and(lastNameStartsSpec())
                .and(citySpec());
    }

    private Specification<Owner> firstNameStartsSpec() {
        return ((root, query, cb) -> StringUtils.hasText(firstNameStarts)
                ? cb.like(cb.lower(root.get("firstName")), firstNameStarts.toLowerCase() + "%")
                : null);
    }

    private Specification<Owner> lastNameStartsSpec() {
        return ((root, query, cb) -> StringUtils.hasText(lastNameStarts)
                ? cb.like(cb.lower(root.get("lastName")), lastNameStarts.toLowerCase() + "%")
                : null);
    }

    private Specification<Owner> citySpec() {
        return ((root, query, cb) -> StringUtils.hasText(city)
                ? cb.equal(cb.lower(root.get("city")), city.toLowerCase())
                : null);
    }
}
