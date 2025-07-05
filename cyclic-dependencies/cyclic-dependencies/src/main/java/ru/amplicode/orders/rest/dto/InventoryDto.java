package ru.amplicode.orders.rest.dto;

import lombok.Value;
import ru.amplicode.orders.domain.City;
import ru.amplicode.orders.domain.Inventory;

/**
 * DTO for {@link Inventory}
 */
@Value
public class InventoryDto {
    Long id;
    Long productId;
    Long available;
    Long reserved;
    CityDto city;

//    demo: Add this during demo
    /**
     * DTO for {@link City}
     */
    @Value
    public static class CityDto {
        Long id;
        String name;
    }
}
