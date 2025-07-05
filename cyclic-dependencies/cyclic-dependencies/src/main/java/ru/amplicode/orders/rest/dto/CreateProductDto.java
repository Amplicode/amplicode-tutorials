package ru.amplicode.orders.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Value;
import ru.amplicode.orders.domain.Product;

import java.math.BigDecimal;

/**
 * DTO for {@link Product}
 */
@Value
public class CreateProductDto {
    @NotBlank
    String name;
    @Positive
    BigDecimal price;
}
