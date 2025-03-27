package org.springframework.samples.petclinic.vet;

import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class VetAutoAssignmentService {

	public static final int SURGERY_ID = 2;
	private final VetRepository vetRepository;

	public VetAutoAssignmentService(VetRepository vetRepository) {
		this.vetRepository = vetRepository;
	}

	public Vet findAppropriateVet(Pet pet) {
		List<Visit> visits = pet.getVisits()
			.stream()
			.toList();
		if (visits.isEmpty() || visits.stream()
			.noneMatch(v -> v.getVet() != null)) {
			return vetRepository.findBySpecialties_IdIn(Collections.singleton(SURGERY_ID))
				.stream()
				.findAny()
				.orElseThrow();
		}
		return visits.stream()
			.filter(v -> v.getVet() != null)
			.max(Comparator.comparing(Visit::getDate))
			.orElseThrow()
			.getVet();
	}
}
