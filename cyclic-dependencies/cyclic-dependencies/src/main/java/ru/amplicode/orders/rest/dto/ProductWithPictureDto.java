package ru.amplicode.orders.rest.dto;

import lombok.Value;

import java.math.BigDecimal;

/**
 * DTO for {@link ru.amplicode.orders.domain.Product}
 */
@Value
public class ProductWithPictureDto {
    Long id;
    String name;
    BigDecimal price;
    String picture;
}