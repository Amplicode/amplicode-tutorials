package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-09T18:35:12+0400",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 20.0.2.1 (Amazon.com Inc.)"
)
@Component
public class VisitMapperImpl implements VisitMapper {

    @Override
    public VisitDto toDto(Visit visit) {
        if ( visit == null ) {
            return null;
        }

        Integer vetId = null;
        Integer id = null;
        LocalDate date = null;
        String description = null;

        vetId = visitVetId( visit );
        id = visit.getId();
        date = visit.getDate();
        description = visit.getDescription();

        VisitDto visitDto = new VisitDto( id, date, description, vetId );

        return visitDto;
    }

    private Integer visitVetId(Visit visit) {
        if ( visit == null ) {
            return null;
        }
        Vet vet = visit.getVet();
        if ( vet == null ) {
            return null;
        }
        Integer id = vet.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
