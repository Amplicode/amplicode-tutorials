package org.springframework.samples.petclinic.owner.rest;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.samples.petclinic.owner.Owner;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface OwnerMapper {
    Owner toEntity(OwnerDto ownerDto);

    OwnerDto toOwnerDto(Owner owner);

    Owner updateWithNull(OwnerDto ownerDto, @MappingTarget Owner owner);

    OwnerExtendedDto toOwnerExtendedDto(Owner owner);

    OwnerMinimalDto toOwnerMinimalDto(Owner owner);
}
