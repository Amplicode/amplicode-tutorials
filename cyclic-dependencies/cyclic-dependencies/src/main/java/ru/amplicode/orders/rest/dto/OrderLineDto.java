package ru.amplicode.orders.rest.dto;

import lombok.Value;
import ru.amplicode.orders.domain.OrderLine;

/**
 * DTO for {@link OrderLine}
 */
@Value
public class OrderLineDto {
    Long id;
    Long productId;
    Long amount;
    Long orderId;
}
