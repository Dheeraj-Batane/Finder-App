package com.db.database.repository;

import com.db.database.entities.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory,Long> {
    boolean existsByNameIgnoreCase(String name);
}
