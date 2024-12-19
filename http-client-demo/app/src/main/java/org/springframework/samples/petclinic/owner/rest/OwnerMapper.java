package org.springframework.samples.petclinic.owner.rest;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.samples.petclinic.owner.Owner;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OwnerMapper {
    OwnerDto toOwnerDto(Owner owner);

    OwnerMinimalDto toOwnerMinimalDto(Owner owner);

    Owner toEntity(OwnerDto ownerDto);

    Owner updateWithNull(OwnerDto ownerDto,
                         @MappingTarget Owner owner);
}
