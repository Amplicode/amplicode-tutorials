package ru.amplicode.orders.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.amplicode.orders.domain.Product;
import ru.amplicode.orders.rest.dto.CreateProductDto;
import ru.amplicode.orders.rest.dto.ProductDto;
import ru.amplicode.orders.rest.dto.ProductWithPictureDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    Product toEntity(ProductDto productDto);

    ProductDto toProductDto(Product product);

    Product toEntity(CreateProductDto createProductDto);

    CreateProductDto toCreateProductDto(Product product);

    Product updateWithNull(ProductDto productDto,
                           @MappingTarget Product product);

    ProductWithPictureDto toProductWithPictureDto(Product product);

    Product updateWithNull(ProductWithPictureDto productWithPictureDto,
                           @MappingTarget Product product);
}
