package com.example.spring_project.repository;

import com.example.spring_project.entity.PaymentTransaction;
import com.example.spring_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByOrderCode(Long orderCode);
    List<PaymentTransaction> findByUserOrderByCreatedAtDesc(User user);
    boolean existsByOrderCode(Long orderCode);
    List<PaymentTransaction> findAllByOrderByCreatedAtDesc();
    List<PaymentTransaction> findByStatusAndCreatedAtBefore(String status, java.time.Instant createdAt);
}
