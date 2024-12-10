package org.springframework.samples.petclinic.vet;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-09T18:35:12+0400",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 20.0.2.1 (Amazon.com Inc.)"
)
@Component
public class VetMapperImpl implements VetMapper {

    @Override
    public Vet toEntity(VetWithoutSalaryDto vetWithoutSalaryDto) {
        if ( vetWithoutSalaryDto == null ) {
            return null;
        }

        Vet vet = new Vet();

        vet.setId( vetWithoutSalaryDto.getId() );
        vet.setFirstName( vetWithoutSalaryDto.getFirstName() );
        vet.setLastName( vetWithoutSalaryDto.getLastName() );
        if ( vet.getSpecialties() != null ) {
            List<Specialty> list = specialtyDtoSetToSpecialtyList( vetWithoutSalaryDto.getSpecialties() );
            if ( list != null ) {
                vet.getSpecialties().addAll( list );
            }
        }

        return vet;
    }

    @Override
    public VetWithoutSalaryDto toDto(Vet vet) {
        if ( vet == null ) {
            return null;
        }

        VetWithoutSalaryDto vetWithoutSalaryDto = new VetWithoutSalaryDto();

        vetWithoutSalaryDto.setId( vet.getId() );
        vetWithoutSalaryDto.setFirstName( vet.getFirstName() );
        vetWithoutSalaryDto.setLastName( vet.getLastName() );
        vetWithoutSalaryDto.setSpecialties( specialtyListToSpecialtyDtoSet( vet.getSpecialties() ) );

        return vetWithoutSalaryDto;
    }

    @Override
    public Vet partialUpdate(VetWithoutSalaryDto vetWithoutSalaryDto, Vet vet) {
        if ( vetWithoutSalaryDto == null ) {
            return vet;
        }

        if ( vetWithoutSalaryDto.getId() != null ) {
            vet.setId( vetWithoutSalaryDto.getId() );
        }
        if ( vetWithoutSalaryDto.getFirstName() != null ) {
            vet.setFirstName( vetWithoutSalaryDto.getFirstName() );
        }
        if ( vetWithoutSalaryDto.getLastName() != null ) {
            vet.setLastName( vetWithoutSalaryDto.getLastName() );
        }
        if ( vet.getSpecialties() != null ) {
            vet.getSpecialties().clear();
            List<Specialty> list = specialtyDtoSetToSpecialtyList( vetWithoutSalaryDto.getSpecialties() );
            if ( list != null ) {
                vet.getSpecialties().addAll( list );
            }
        }

        return vet;
    }

    protected Specialty specialtyDtoToSpecialty(VetWithoutSalaryDto.SpecialtyDto specialtyDto) {
        if ( specialtyDto == null ) {
            return null;
        }

        Specialty specialty = new Specialty();

        specialty.setId( specialtyDto.getId() );
        specialty.setName( specialtyDto.getName() );

        return specialty;
    }

    protected List<Specialty> specialtyDtoSetToSpecialtyList(Set<VetWithoutSalaryDto.SpecialtyDto> set) {
        if ( set == null ) {
            return null;
        }

        List<Specialty> list = new ArrayList<Specialty>( set.size() );
        for ( VetWithoutSalaryDto.SpecialtyDto specialtyDto : set ) {
            list.add( specialtyDtoToSpecialty( specialtyDto ) );
        }

        return list;
    }

    protected VetWithoutSalaryDto.SpecialtyDto specialtyToSpecialtyDto(Specialty specialty) {
        if ( specialty == null ) {
            return null;
        }

        VetWithoutSalaryDto.SpecialtyDto specialtyDto = new VetWithoutSalaryDto.SpecialtyDto();

        specialtyDto.setId( specialty.getId() );
        specialtyDto.setName( specialty.getName() );

        return specialtyDto;
    }

    protected Set<VetWithoutSalaryDto.SpecialtyDto> specialtyListToSpecialtyDtoSet(List<Specialty> list) {
        if ( list == null ) {
            return null;
        }

        Set<VetWithoutSalaryDto.SpecialtyDto> set = new LinkedHashSet<VetWithoutSalaryDto.SpecialtyDto>( Math.max( (int) ( list.size() / .75f ) + 1, 16 ) );
        for ( Specialty specialty : list ) {
            set.add( specialtyToSpecialtyDto( specialty ) );
        }

        return set;
    }
}
