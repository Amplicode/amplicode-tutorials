package ru.amplicode.orders.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.amplicode.orders.domain.Inventory;
import ru.amplicode.orders.rest.dto.InventoryDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryMapper {
    @Mapping(source = "productId", target = "product.id")
    Inventory toEntity(InventoryDto inventoryDto);

    @Mapping(source = "product.id", target = "productId")
    InventoryDto toInventoryDto(Inventory inventory);
}
