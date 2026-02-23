package com.example.demo.controller;

import com.example.demo.entity.Book;
import com.example.demo.entity.BorrowRecord;
import com.example.demo.service.BookService;
import com.example.demo.service.BorrowService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/librarian")
@PreAuthorize("hasRole('LIBRARIAN')")
public class LibrarianController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BorrowService borrowService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Book> allBooks = bookService.findAllBooks();
        List<BorrowRecord> borrowedBooks = borrowService.getAllBorrowedBooks();
        List<BorrowRecord> overdueBooks = borrowService.getOverdueBooks();
        
        model.addAttribute("totalBooks", allBooks.size());
        model.addAttribute("borrowedBooksCount", borrowedBooks.size());
        model.addAttribute("overdueCount", overdueBooks.size());
        model.addAttribute("recentBorrows", borrowedBooks.stream().limit(5).toList());
        
        return "librarian/dashboard";
    }
    
    @GetMapping("/books")
    public String manageBooks(@RequestParam(required = false) String search, Model model) {
        List<Book> books;
        if (search != null && !search.trim().isEmpty()) {
            books = bookService.searchBooks(search);
        } else {
            books = bookService.findAllBooks();
        }
        
        model.addAttribute("books", books);
        model.addAttribute("search", search);
        model.addAttribute("categories", bookService.getAllCategories());
        
        return "librarian/books";
    }
    
    @GetMapping("/books/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "librarian/add-book";
    }
    
    @PostMapping("/books/add")
    public String addBook(@ModelAttribute Book book, RedirectAttributes redirectAttributes) {
        try {
            bookService.saveBook(book);
            redirectAttributes.addFlashAttribute("success", "Book added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding book: " + e.getMessage());
        }
        return "redirect:/librarian/books";
    }
    
    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Book> book = bookService.findById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "librarian/edit-book";
        } else {
            redirectAttributes.addFlashAttribute("error", "Book not found!");
            return "redirect:/librarian/books";
        }
    }
    
    @PostMapping("/books/edit/{id}")
    public String editBook(@PathVariable Long id, @ModelAttribute Book book, RedirectAttributes redirectAttributes) {
        try {
            book.setId(id);
            bookService.saveBook(book);
            redirectAttributes.addFlashAttribute("success", "Book updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating book: " + e.getMessage());
        }
        return "redirect:/librarian/books";
    }
    
    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("success", "Book deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting book: " + e.getMessage());
        }
        return "redirect:/librarian/books";
    }
    
    @GetMapping("/borrowed-books")
    public String viewBorrowedBooks(Model model) {
        List<BorrowRecord> borrowedBooks = borrowService.getAllBorrowedBooks();
        List<BorrowRecord> overdueBooks = borrowService.getOverdueBooks();
        
        model.addAttribute("borrowedBooks", borrowedBooks);
        model.addAttribute("overdueBooks", overdueBooks);
        
        return "librarian/borrowed-books";
    }
    
    @GetMapping("/profile")
    public String profile(Model model) {
        // Get statistics for the profile page
        List<Book> allBooks = bookService.findAllBooks();
        List<BorrowRecord> borrowedBooks = borrowService.getAllBorrowedBooks();
        List<BorrowRecord> overdueBooks = borrowService.getOverdueBooks();
        
        model.addAttribute("totalBooks", allBooks.size());
        model.addAttribute("borrowedBooksCount", borrowedBooks.size());
        model.addAttribute("overdueCount", overdueBooks.size());
        model.addAttribute("totalUsers", userService.findAllUsers().size());
        
        return "librarian/profile-simple";
    }
    
    @GetMapping("/edit-profile")
    public String editProfile(Model model) {
        return "librarian/edit-profile";
    }
    
    @GetMapping("/change-password")
    public String changePassword(Model model) {
        return "librarian/change-password";
    }
    
    @GetMapping("/notifications")
    public String notifications(Model model) {
        return "librarian/notifications";
    }
}
