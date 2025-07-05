package ru.amplicode.orders.rest.dto;

import lombok.Value;
import ru.amplicode.orders.domain.Order;
import ru.amplicode.orders.domain.OrderStatus;

import java.math.BigDecimal;

/**
 * DTO for {@link Order}
 */
@Value
public class OrderDto {
    Long id;
    Long customerId;
    OrderStatus orderStatus;
    BigDecimal sum;
}
