package org.springframework.samples.petclinic.owner;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link Visit}
 */
public record VisitDto(Integer id, LocalDate date, @NotBlank String description,
					   Integer vetId) implements Serializable {
}
