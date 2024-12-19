package org.springframework.samples.petclinic.owner.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/rest/owners")
@RequiredArgsConstructor
public class OwnerRestController {

    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;
    private final ObjectMapper objectMapper;

    @GetMapping("/{id}")
    public OwnerDto getOne(@PathVariable Integer id) {
        Optional<Owner> ownerOptional = ownerRepository.findById(id);
        return ownerMapper.toOwnerDto(ownerOptional.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner with id `%d` not found".formatted(id))
        ));
    }

    @GetMapping
    public PagedModel<OwnerMinimalDto> getAll(@ModelAttribute OwnerFilter filter,
                                              Pageable pageable) {
        Specification<Owner> spec = filter.toSpecification();
        Page<Owner> owners = ownerRepository.findAll(spec, pageable);
        Page<OwnerMinimalDto> ownerMinimalDtoPage = owners.map(ownerMapper::toOwnerMinimalDto);
        return new PagedModel<>(ownerMinimalDtoPage);
    }

    @PostMapping
    public OwnerDto create(@RequestBody @Valid OwnerDto dto) {
        if (dto.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must be null");
        }
        Owner owner = ownerMapper.toEntity(dto);
        Owner resultOwner = ownerRepository.save(owner);
        return ownerMapper.toOwnerDto(resultOwner);
    }

    @PutMapping("/{id}")
    public OwnerDto update(@PathVariable Integer id,
                           @RequestBody @Valid OwnerDto dto) {
        if (!dto.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id in request path and body must be equal");
        }
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
        ownerMapper.updateWithNull(dto, owner);
        Owner resultOwner = ownerRepository.save(owner);
        return ownerMapper.toOwnerDto(resultOwner);
    }

    @PatchMapping("/{id}")
    public OwnerDto patch(@PathVariable Integer id,
                          @RequestBody JsonNode patchNode) throws IOException {
        if (patchNode.get("id") != null && patchNode.get("id").asInt() != id) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id in request path and body must be equal");
        }
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        OwnerDto ownerDto = ownerMapper.toOwnerDto(owner);
        objectMapper.readerForUpdating(ownerDto)
                .readValue(patchNode);
        ownerMapper.updateWithNull(ownerDto, owner);

        Owner resultOwner = ownerRepository.save(owner);
        return ownerMapper.toOwnerDto(resultOwner);
    }

    @DeleteMapping("/{id}")
    public OwnerMinimalDto delete(@PathVariable Integer id) {
        Owner owner = ownerRepository.findById(id)
                .orElse(null);
        if (owner != null) {
            ownerRepository.delete(owner);
        }
        return ownerMapper.toOwnerMinimalDto(owner);
    }

    //See crud-methods-generation/spring-petclinic-crud-methods/generated-requests.connekt.kts
    //to execute HTTP requests via Amplicode HTTP-client

    //See crud-methods-generation/spring-petclinic-crud-methods/src/test/java/org/springframework/samples/petclinic/owner/rest/OwnerRestControllerTest.java
    //to execute Spring Web Tests
}
