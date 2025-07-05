package ru.amplicode.orders.rest.dto;

import ru.amplicode.orders.domain.Order;

/**
 * DTO for {@link Order}
 */
public record CreateOrderDto(Long customerId, Long cityId) {
}
