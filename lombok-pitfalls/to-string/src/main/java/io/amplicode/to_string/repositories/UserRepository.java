package io.amplicode.to_string.repositories;

import io.amplicode.to_string.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}