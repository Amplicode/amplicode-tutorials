package org.springframework.samples.petclinic.vet;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VetMapper {
	Vet toEntity(VetWithoutSalaryDto vetWithoutSalaryDto);

	VetWithoutSalaryDto toDto(Vet vet);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Vet partialUpdate(VetWithoutSalaryDto vetWithoutSalaryDto,
					  @MappingTarget Vet vet);
}
