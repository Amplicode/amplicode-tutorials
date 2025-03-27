package org.springframework.samples.petclinic.owner.rest;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * DTO for {@link Owner}
 */
public record OwnerExtendedDto(Integer id, @NotBlank String firstName, @NotBlank String lastName,
                               @NotBlank String address, @NotBlank String city,
                               @Digits(integer = 10, fraction = 0) @NotBlank String telephone, List<PetDto> pets) {
    /**
     * DTO for {@link Pet}
     */
    public record PetDto(Integer id, String name, LocalDate birthDate, Integer typeId, Set<VisitDto> visits) {
        /**
         * DTO for {@link Visit}
         */
        public record VisitDto(Integer id, LocalDate date, @NotBlank String description, VetDto vet) {
            /**
             * DTO for {@link org.springframework.samples.petclinic.vet.Vet}
             */
            public record VetDto(Integer id, @NotBlank String firstName, @NotBlank String lastName) {
            }
        }
    }
}
