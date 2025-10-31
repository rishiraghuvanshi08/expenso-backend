package com.expenso.Expenso.repository;

import com.expenso.Expenso.entities.AppUser;
import com.expenso.Expenso.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link AppUser} entities.
 * Provides methods to perform CRUD operations and custom lookups on application users.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

  /**
   * Finds a user by their registered email address.
   */
  Optional<AppUser> findByEmail(String email);
}