package org.springframework.samples.petclinic.vet;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.owner.VisitDto;
import org.springframework.samples.petclinic.owner.VisitMapper;
import org.springframework.stereotype.Component;

@Component
public class VisitHibernateEventListener implements PostDeleteEventListener, PostInsertEventListener, PostUpdateEventListener {

	private final EntityManagerFactory entityManagerFactory;

	private final VisitMapper visitMapper;

	private final KafkaTemplate<String, VisitDto> kafkaTemplate;

	public VisitHibernateEventListener(EntityManagerFactory entityManagerFactory,
									   VisitMapper visitMapper,
									   KafkaTemplate<String, VisitDto> kafkaTemplate) {
		this.entityManagerFactory = entityManagerFactory;
		this.visitMapper = visitMapper;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public void onPostDelete(PostDeleteEvent postDeleteEvent) {
		Object entity = postDeleteEvent.getEntity();
		if (entity instanceof Visit visit) {
			kafkaTemplate.send("visit", visitMapper.toDto(visit));
		}

	}

	@Override
	public void onPostInsert(PostInsertEvent postInsertEvent) {
		Object entity = postInsertEvent.getEntity();
		if (entity instanceof Visit visit) {
			kafkaTemplate.send("visit", visitMapper.toDto(visit));
		}

	}

	@Override
	public void onPostUpdate(PostUpdateEvent postUpdateEvent) {
		Object entity = postUpdateEvent.getEntity();
		if (entity instanceof Visit visit) {
			kafkaTemplate.send("visit", visitMapper.toDto(visit));
		}

	}

	@Override
	public boolean requiresPostCommitHandling(EntityPersister entityPersister) {
		return true;
	}

	@PostConstruct
	private void postConstruct() {
		SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
		EventListenerRegistry registry = sessionFactory
			.getServiceRegistry()
			.getService(EventListenerRegistry.class);
		assert registry != null;
		registry.prependListeners(EventType.POST_COMMIT_DELETE, this);
		registry.prependListeners(EventType.POST_COMMIT_INSERT, this);
		registry.prependListeners(EventType.POST_COMMIT_UPDATE, this);
	}
}
