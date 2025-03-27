package org.springframework.samples.petclinic.owner.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class OwnerRestController {

    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;
    private final ObjectMapper objectMapper;

    public OwnerRestController(OwnerRepository ownerRepository,
                               OwnerMapper ownerMapper,
                               ObjectMapper objectMapper) {
        this.ownerRepository = ownerRepository;
        this.ownerMapper = ownerMapper;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public OwnerDto create(@RequestBody @Valid OwnerDto ownerDto) {
        if (ownerDto.getId() != null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Id must be null");
        }
        Owner owner = ownerMapper.toEntity(ownerDto);
        Owner saved = ownerRepository.save(owner);
        return ownerMapper.toOwnerDto(saved);
    }

    @GetMapping("/{id}")
    public OwnerMinimalDto getOne(@PathVariable Integer id) {
        Optional<Owner> ownerOptional = ownerRepository.findById(id);
        return ownerMapper.toOwnerMinimalDto(ownerOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @GetMapping
    public PagedModel<OwnerMinimalDto> getAll(@ModelAttribute OwnerFilter ownerFilter,
                                              Pageable pageable) {
        Page<Owner> owners = ownerRepository.findAll(ownerFilter.toSpecification(), pageable);
        var ownerDtoPage = owners.map(ownerMapper::toOwnerMinimalDto);
        return new PagedModel<>(ownerDtoPage);
    }

    @PutMapping("/{id}")
    public OwnerDto update(@PathVariable Integer id,
                           @RequestBody @Valid OwnerDto dto) {
        if (!dto.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id in request body and path variable must be equal");
        }
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))
                );
        ownerMapper.updateWithNull(dto, owner);
        Owner resultOwner = ownerRepository.save(owner);
        return ownerMapper.toOwnerDto(resultOwner);
    }

    @PatchMapping("/{id}")
    public OwnerDto patch(@PathVariable Integer id,
                          @RequestBody JsonNode patchNode) throws IOException {
        if (patchNode.get("id") == null || patchNode.get("id").asInt() != id) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id in request body and path variable must be equal");
        }
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))
                );

        OwnerDto ownerDto = ownerMapper.toOwnerDto(owner);
        objectMapper.readerForUpdating(ownerDto)
                .readValue(patchNode);
        ownerMapper.updateWithNull(ownerDto, owner);

        Owner resultOwner = ownerRepository.save(owner);
        return ownerMapper.toOwnerDto(resultOwner);
    }

    @DeleteMapping("/{id}")
    public OwnerDto delete(@PathVariable Integer id) {
        Owner owner = ownerRepository.findById(id)
                .orElse(null);
        if (owner != null) {
            ownerRepository.delete(owner);
        }
        return ownerMapper.toOwnerDto(owner);
    }
}
