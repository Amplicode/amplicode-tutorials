package org.springframework.samples.petclinic.owner.rest;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 * DTO for {@link org.springframework.samples.petclinic.owner.Owner}
 */
@Value
public class OwnerDto {
    Integer id;
    @NotBlank
    String firstName;
    String lastName;
    String address;
    @NotBlank
    String city;
    String telephone;
}
