package ru.amplicode.orders.rest.dto;

import lombok.Value;
import ru.amplicode.orders.domain.Product;

import java.math.BigDecimal;

/**
 * DTO for {@link Product}
 */
@Value
public class ProductDto {
    Long id;
    String name;
    BigDecimal price;
}
