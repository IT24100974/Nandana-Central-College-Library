package com.example.demo.service;

import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
    
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }
    
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }
    
    public List<Book> findAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0);
    }
    
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllBooks();
        }
        return bookRepository.searchBooks(keyword.trim());
    }
    
    public List<Book> findByCategory(String category) {
        return bookRepository.findByCategory(category);
    }
    
    public List<String> getAllCategories() {
        return bookRepository.findAllCategories();
    }
    
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
    
    public boolean canBorrowBook(Long bookId) {
        Optional<Book> book = findById(bookId);
        return book.isPresent() && book.get().getAvailableCopies() > 0;
    }
    
    public void decreaseAvailableCopies(Long bookId) {
        Optional<Book> bookOpt = findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (book.getAvailableCopies() > 0) {
                book.setAvailableCopies(book.getAvailableCopies() - 1);
                saveBook(book);
            }
        }
    }
    
    public void increaseAvailableCopies(Long bookId) {
        Optional<Book> bookOpt = findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (book.getAvailableCopies() < book.getTotalCopies()) {
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                saveBook(book);
            }
        }
    }
    
    public void createSampleBooks() {
        if (bookRepository.count() == 0) {
            // Create some sample books
            Book book1 = new Book("Java: The Complete Reference", "Herbert Schildt", "978-1260440232", "Programming", "Comprehensive guide to Java programming", 5);
            Book book2 = new Book("Clean Code", "Robert C. Martin", "978-0132350884", "Programming", "A handbook of agile software craftsmanship", 3);
            Book book3 = new Book("Introduction to Algorithms", "Thomas H. Cormen", "978-0262033848", "Computer Science", "Comprehensive introduction to algorithms", 4);
            Book book4 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", "Literature", "Classic American novel", 6);
            Book book5 = new Book("To Kill a Mockingbird", "Harper Lee", "978-0061120084", "Literature", "Pulitzer Prize winning novel", 4);
            
            saveBook(book1);
            saveBook(book2);
            saveBook(book3);
            saveBook(book4);
            saveBook(book5);
        }
    }
}
