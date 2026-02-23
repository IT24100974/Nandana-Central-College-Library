package com.example.demo.repository;

import com.example.demo.entity.BorrowRecord;
import com.example.demo.entity.User;
import com.example.demo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUserAndStatus(User user, BorrowRecord.Status status);
    List<BorrowRecord> findByUser(User user);
    List<BorrowRecord> findByStatus(BorrowRecord.Status status);
    Optional<BorrowRecord> findByUserAndBookAndStatus(User user, Book book, BorrowRecord.Status status);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWED' AND br.dueDate < CURRENT_TIMESTAMP")
    List<BorrowRecord> findOverdueRecords();
    
    long countByUserAndStatus(User user, BorrowRecord.Status status);
}
