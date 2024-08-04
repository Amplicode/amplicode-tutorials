package io.amplicode.data.repositories;

import io.amplicode.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}