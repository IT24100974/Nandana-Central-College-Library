package com.example.demo.repository;

import com.example.demo.entity.BookReview;
import com.example.demo.entity.Book;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Long> {
    List<BookReview> findByBook(Book book);
    List<BookReview> findByUser(User user);
    Optional<BookReview> findByUserAndBook(User user, Book book);
    
    @Query("SELECT AVG(br.rating) FROM BookReview br WHERE br.book = :book")
    Double getAverageRatingForBook(@Param("book") Book book);
    
    @Query("SELECT COUNT(br) FROM BookReview br WHERE br.book = :book")
    Long getReviewCountForBook(@Param("book") Book book);
    
    @Query("SELECT br FROM BookReview br ORDER BY br.createdAt DESC")
    List<BookReview> findRecentReviews();
}
