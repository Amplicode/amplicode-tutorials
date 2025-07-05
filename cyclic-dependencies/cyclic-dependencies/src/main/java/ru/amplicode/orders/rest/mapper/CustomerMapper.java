package ru.amplicode.orders.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import ru.amplicode.orders.domain.Customer;
import ru.amplicode.orders.rest.dto.CreateCustomerDto;
import ru.amplicode.orders.rest.dto.CustomerDto;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {
    CustomerDto customerToCustomerDto(Customer customer);

    @Mapping(target = "id", ignore = true)
    Customer toEntity(CreateCustomerDto dto);

    Customer toEntity(CustomerDto customerDto);
}
