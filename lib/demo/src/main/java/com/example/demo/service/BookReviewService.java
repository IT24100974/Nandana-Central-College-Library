package com.example.demo.service;

import com.example.demo.entity.BookReview;
import com.example.demo.entity.Book;
import com.example.demo.entity.User;
import com.example.demo.repository.BookReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookReviewService {
    
    @Autowired
    private BookReviewRepository bookReviewRepository;
    
    public BookReview saveReview(BookReview review) {
        return bookReviewRepository.save(review);
    }
    
    public Optional<BookReview> findById(Long id) {
        return bookReviewRepository.findById(id);
    }
    
    public List<BookReview> getReviewsForBook(Book book) {
        return bookReviewRepository.findByBook(book);
    }
    
    public List<BookReview> getReviewsByUser(User user) {
        return bookReviewRepository.findByUser(user);
    }
    
    public Optional<BookReview> getUserReviewForBook(User user, Book book) {
        return bookReviewRepository.findByUserAndBook(user, book);
    }
    
    public Double getAverageRatingForBook(Book book) {
        Double average = bookReviewRepository.getAverageRatingForBook(book);
        return average != null ? average : 0.0;
    }
    
    public Long getReviewCountForBook(Book book) {
        return bookReviewRepository.getReviewCountForBook(book);
    }
    
    public List<BookReview> getRecentReviews() {
        return bookReviewRepository.findRecentReviews();
    }
    
    public BookReview addOrUpdateReview(User user, Book book, Integer rating, String reviewText) {
        Optional<BookReview> existingReview = getUserReviewForBook(user, book);
        
        if (existingReview.isPresent()) {
            BookReview review = existingReview.get();
            review.setRating(rating);
            review.setReviewText(reviewText);
            review.preUpdate();
            return saveReview(review);
        } else {
            BookReview newReview = new BookReview(user, book, rating, reviewText);
            return saveReview(newReview);
        }
    }
    
    public void deleteReview(Long id) {
        bookReviewRepository.deleteById(id);
    }
    
    public boolean canUserReviewBook(User user, Book book) {
        // User can review if they have borrowed the book before
        // This logic can be enhanced based on business requirements
        return true;
    }
}
