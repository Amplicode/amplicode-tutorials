package io.amplicode.equals_and_hash_code.repositories;


import io.amplicode.equals_and_hash_code.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}