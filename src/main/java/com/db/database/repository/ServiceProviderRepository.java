package com.db.database.repository;

import com.db.database.entities.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider,Long> {
    boolean existsByUserId(Long userId);
    // --- NEW ADDITION ---
    // Spring Data JPA automatically traverses the Many-to-Many relationship
    List<ServiceProvider> findByServiceCategoriesId(Long categoryId);
}
