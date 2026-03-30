package com.db.database.repository;

import com.db.database.entities.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews,Long> {
    boolean existsByBookingId(Long bookingId);
    Optional<Reviews> findByBookingId(Long bookingId);
    List<Reviews> findByBookingServiceProviderIdOrderByCreateDateDesc(Long providerId);
}
