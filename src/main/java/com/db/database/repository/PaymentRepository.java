package com.db.database.repository;

import com.db.database.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    // Prevents saving the same transaction twice
    boolean existsByTxNumber(String txNumber);
}
