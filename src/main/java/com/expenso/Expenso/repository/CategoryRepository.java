package com.expenso.Expenso.repository;

import com.expenso.Expenso.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
