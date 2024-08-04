package org.springframework.samples.petclinic.vet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.samples.petclinic.owner.VisitDto;
import org.springframework.stereotype.Service;

@Service
public class KafkaVisitService {
	private static final Logger log = LoggerFactory.getLogger(KafkaVisitService.class);

	@KafkaListener(topics = "visit", containerFactory = "visitDtoListenerFactory")
	public void consumeVisitDto(VisitDto visitDto) {
		log.info(visitDto.toString());
	}
}
