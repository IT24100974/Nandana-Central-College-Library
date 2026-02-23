package com.example.demo.service;

import com.example.demo.entity.BorrowRecord;
import com.example.demo.entity.User;
import com.example.demo.entity.Book;
import com.example.demo.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowService {
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    
    @Autowired
    private BookService bookService;
    
    private static final int MAX_BOOKS_PER_USER = 5;
    
    @Transactional
    public BorrowRecord borrowBook(User user, Book book) {
        // Check if user has already borrowed this book
        Optional<BorrowRecord> existingRecord = borrowRecordRepository
            .findByUserAndBookAndStatus(user, book, BorrowRecord.Status.BORROWED);
        
        if (existingRecord.isPresent()) {
            throw new RuntimeException("You have already borrowed this book");
        }
        
        // Check if user has reached maximum borrowing limit
        long currentBorrowedCount = borrowRecordRepository.countByUserAndStatus(user, BorrowRecord.Status.BORROWED);
        if (currentBorrowedCount >= MAX_BOOKS_PER_USER) {
            throw new RuntimeException("You have reached the maximum borrowing limit of " + MAX_BOOKS_PER_USER + " books");
        }
        
        // Check if book is available
        if (!bookService.canBorrowBook(book.getId())) {
            throw new RuntimeException("This book is not available for borrowing");
        }
        
        // Create borrow record
        BorrowRecord borrowRecord = new BorrowRecord(user, book);
        borrowRecord = borrowRecordRepository.save(borrowRecord);
        
        // Decrease available copies
        bookService.decreaseAvailableCopies(book.getId());
        
        return borrowRecord;
    }
    
    @Transactional
    public BorrowRecord returnBook(User user, Book book) {
        Optional<BorrowRecord> recordOpt = borrowRecordRepository
            .findByUserAndBookAndStatus(user, book, BorrowRecord.Status.BORROWED);
        
        if (!recordOpt.isPresent()) {
            throw new RuntimeException("No active borrow record found for this book");
        }
        
        BorrowRecord record = recordOpt.get();
        record.setReturnDate(LocalDateTime.now());
        record.setStatus(BorrowRecord.Status.RETURNED);
        
        // Increase available copies
        bookService.increaseAvailableCopies(book.getId());
        
        return borrowRecordRepository.save(record);
    }
    
    public List<BorrowRecord> getUserBorrowedBooks(User user) {
        return borrowRecordRepository.findByUserAndStatus(user, BorrowRecord.Status.BORROWED);
    }
    
    public List<BorrowRecord> getUserBorrowHistory(User user) {
        return borrowRecordRepository.findByUser(user);
    }
    
    public List<BorrowRecord> getAllBorrowedBooks() {
        return borrowRecordRepository.findByStatus(BorrowRecord.Status.BORROWED);
    }
    
    public List<BorrowRecord> getOverdueBooks() {
        return borrowRecordRepository.findOverdueRecords();
    }
    
    public boolean hasUserBorrowedBook(User user, Book book) {
        return borrowRecordRepository.findByUserAndBookAndStatus(user, book, BorrowRecord.Status.BORROWED).isPresent();
    }
}
