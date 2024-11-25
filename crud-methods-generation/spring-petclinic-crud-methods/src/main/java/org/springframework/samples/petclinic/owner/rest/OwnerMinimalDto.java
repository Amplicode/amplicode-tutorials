package org.springframework.samples.petclinic.owner.rest;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 * DTO for {@link org.springframework.samples.petclinic.owner.Owner}
 */
@Value
public class OwnerMinimalDto {
    Integer id;
    @NotBlank
    String firstName;
    String lastName;
    @NotBlank
    String city;
}
