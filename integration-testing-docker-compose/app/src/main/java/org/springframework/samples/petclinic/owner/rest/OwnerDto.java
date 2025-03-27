package org.springframework.samples.petclinic.owner.rest;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerDto {
    private Integer id;
    private @NotBlank String firstName;
    private @NotBlank String lastName;
    private @NotBlank String address;
    private @NotBlank String city;
    private @Digits(integer = 10, fraction = 0)
    @NotBlank String telephone;
}
