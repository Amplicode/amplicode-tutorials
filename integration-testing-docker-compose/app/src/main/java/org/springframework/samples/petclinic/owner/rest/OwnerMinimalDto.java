package org.springframework.samples.petclinic.owner.rest;

import jakarta.validation.constraints.NotBlank;
import org.springframework.samples.petclinic.owner.Owner;

/**
 * DTO for {@link Owner}
 */
public record OwnerMinimalDto(Integer id,
                              @NotBlank String firstName,
                              @NotBlank String lastName,
                              @NotBlank String city) {
}
