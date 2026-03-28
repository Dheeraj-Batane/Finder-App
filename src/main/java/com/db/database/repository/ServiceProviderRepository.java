package com.db.database.repository;

import com.db.database.entities.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider,Long> {
    boolean existsByUserId(Long userId);
}
