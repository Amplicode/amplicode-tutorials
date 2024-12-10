package org.springframework.samples.petclinic.owner.rest;

import javax.annotation.processing.Generated;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-09T18:35:11+0400",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 20.0.2.1 (Amazon.com Inc.)"
)
@Component
public class OwnerMapperImpl implements OwnerMapper {

    @Override
    public OwnerDto toOwnerDto(Owner owner) {
        if ( owner == null ) {
            return null;
        }

        Integer id = null;
        String firstName = null;
        String lastName = null;
        String address = null;
        String city = null;
        String telephone = null;

        id = owner.getId();
        firstName = owner.getFirstName();
        lastName = owner.getLastName();
        address = owner.getAddress();
        city = owner.getCity();
        telephone = owner.getTelephone();

        OwnerDto ownerDto = new OwnerDto( id, firstName, lastName, address, city, telephone );

        return ownerDto;
    }

    @Override
    public OwnerMinimalDto toOwnerMinimalDto(Owner owner) {
        if ( owner == null ) {
            return null;
        }

        Integer id = null;
        String firstName = null;
        String lastName = null;
        String city = null;

        id = owner.getId();
        firstName = owner.getFirstName();
        lastName = owner.getLastName();
        city = owner.getCity();

        OwnerMinimalDto ownerMinimalDto = new OwnerMinimalDto( id, firstName, lastName, city );

        return ownerMinimalDto;
    }

    @Override
    public Owner toEntity(OwnerDto ownerDto) {
        if ( ownerDto == null ) {
            return null;
        }

        Owner owner = new Owner();

        owner.setId( ownerDto.getId() );
        owner.setFirstName( ownerDto.getFirstName() );
        owner.setLastName( ownerDto.getLastName() );
        owner.setAddress( ownerDto.getAddress() );
        owner.setCity( ownerDto.getCity() );
        owner.setTelephone( ownerDto.getTelephone() );

        return owner;
    }

    @Override
    public Owner updateWithNull(OwnerDto ownerDto, Owner owner) {
        if ( ownerDto == null ) {
            return owner;
        }

        owner.setId( ownerDto.getId() );
        owner.setFirstName( ownerDto.getFirstName() );
        owner.setLastName( ownerDto.getLastName() );
        owner.setAddress( ownerDto.getAddress() );
        owner.setCity( ownerDto.getCity() );
        owner.setTelephone( ownerDto.getTelephone() );

        return owner;
    }
}
