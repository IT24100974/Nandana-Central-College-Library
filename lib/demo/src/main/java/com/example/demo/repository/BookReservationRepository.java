package com.example.demo.repository;

import com.example.demo.entity.BookReservation;
import com.example.demo.entity.Book;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookReservationRepository extends JpaRepository<BookReservation, Long> {
    List<BookReservation> findByUser(User user);
    List<BookReservation> findByBook(Book book);
    List<BookReservation> findByStatus(BookReservation.Status status);
    Optional<BookReservation> findByUserAndBookAndStatus(User user, Book book, BookReservation.Status status);
    
    @Query("SELECT br FROM BookReservation br WHERE br.status = 'ACTIVE' AND br.expiresAt < CURRENT_TIMESTAMP")
    List<BookReservation> findExpiredReservations();
    
    @Query("SELECT br FROM BookReservation br WHERE br.status = 'ACTIVE' ORDER BY br.reservationDate ASC")
    List<BookReservation> findActiveReservationsOrderByDate();
}
