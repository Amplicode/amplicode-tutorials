package io.amplicode.builder_and_allargsconstructor.repositories;

import io.amplicode.builder_and_allargsconstructor.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}