package org.springframework.samples.petclinic.vet;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/rest/vets")
public class VetRestController {

	private final VetRepository vetRepository;

	private final VetMapper vetMapper;

	private final SpecialtyRepository specialtyRepository;

	public VetRestController(VetRepository vetRepository,
							 VetMapper vetMapper,
							 SpecialtyRepository specialtyRepository) {
		this.vetRepository = vetRepository;
		this.vetMapper = vetMapper;
		this.specialtyRepository = specialtyRepository;
	}

	@GetMapping
	public Collection<VetWithoutSalaryDto> findAll() throws DataAccessException {
		Collection<Vet> vets = vetRepository.findAll();
		Collection<VetWithoutSalaryDto> vetWithoutSalaryDtos = vets.stream()
			.map(vetMapper::toDto)
			.toList();
		return vetWithoutSalaryDtos;
	}

	@GetMapping(path = {"/by-specialty"})
	public List<VetWithoutSalaryDto> findBySpecialtiesIn(@RequestParam Collection<Integer> specialtyIds) {
		Collection<Specialty> specialties = specialtyIds.stream()
			.map(specialtyRepository::getReferenceById)
			.toList();
		List<Vet> vets = vetRepository.findBySpecialtiesIn(specialties);
		List<VetWithoutSalaryDto> vetWithoutSalaryDtos = vets.stream()
			.map(vetMapper::toDto)
			.toList();
		return vetWithoutSalaryDtos;
	}
}

