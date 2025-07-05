package ru.amplicode.orders.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.amplicode.orders.domain.OrderLine;
import ru.amplicode.orders.rest.dto.OrderLineDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderLineMapper {
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "order.id", target = "orderId")
    OrderLineDto toOrderLineDto(OrderLine orderLine);

    OrderLine toEntity(OrderLineDto orderLineDto);
}
