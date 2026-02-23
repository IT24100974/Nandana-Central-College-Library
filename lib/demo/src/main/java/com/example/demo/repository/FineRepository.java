package com.example.demo.repository;

import com.example.demo.entity.Fine;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {
    List<Fine> findByUser(User user);
    List<Fine> findByStatus(Fine.Status status);
    List<Fine> findByUserAndStatus(User user, Fine.Status status);
    
    @Query("SELECT SUM(f.amount) FROM Fine f WHERE f.user = :user AND f.status = 'PENDING'")
    BigDecimal getTotalPendingFinesForUser(User user);
    
    @Query("SELECT f FROM Fine f WHERE f.status = 'PENDING' ORDER BY f.createdAt DESC")
    List<Fine> findAllPendingFines();
}
