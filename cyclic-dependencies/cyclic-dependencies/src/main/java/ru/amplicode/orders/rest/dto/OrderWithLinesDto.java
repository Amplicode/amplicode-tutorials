package ru.amplicode.orders.rest.dto;

import lombok.Value;
import ru.amplicode.orders.domain.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * DTO for {@link Order}
 */
@Value
public class OrderWithLinesDto {
    Long id;
    OrderStatus orderStatus;
    List<OrderLineDto> orderLines;
    BigDecimal sum;
    CityDto city;
    Instant createdDate;

    /**
     * DTO for {@link OrderLine}
     */
    @Value
    public static class OrderLineDto {
        Long id;
        ProductDto product;
        Long amount;

        /**
         * DTO for {@link Product}
         */
        @Value
        public static class ProductDto {
            Long id;
            String name;
            BigDecimal price;
        }
    }

    /**
     * DTO for {@link City}
     */
    @Value
    public static class CityDto {
        Long id;
        String name;
    }
}
