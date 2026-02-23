package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BorrowService borrowService;
    
    @Autowired
    private BookReviewService bookReviewService;
    
    @Autowired
    private UserService userService;
    
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName()).orElse(null);
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            List<BorrowRecord> borrowedBooks = borrowService.getUserBorrowedBooks(currentUser);
            model.addAttribute("user", currentUser);
            model.addAttribute("borrowedBooks", borrowedBooks);
            model.addAttribute("borrowedCount", borrowedBooks.size());
        }
        return "student/dashboard";
    }
    
    @GetMapping("/browse")
    public String browseBooks(@RequestParam(required = false) String search, 
                             @RequestParam(required = false) String category, 
                             Model model) {
        try {
            System.out.println("=== BROWSE CONTROLLER CALLED ===");
            
            // Start with empty lists to avoid null issues
            List<Book> books = new java.util.ArrayList<>();
            List<String> categories = new java.util.ArrayList<>();
            User currentUser = null;
            
            try {
                currentUser = getCurrentUser();
                System.out.println("Current user: " + (currentUser != null ? currentUser.getUsername() : "null"));
            } catch (Exception e) {
                System.err.println("Error getting current user: " + e.getMessage());
            }
            
            try {
                books = bookService.findAllBooks();
                System.out.println("Books loaded: " + (books != null ? books.size() : "null"));
            } catch (Exception e) {
                System.err.println("Error loading books: " + e.getMessage());
                e.printStackTrace();
            }
            
            try {
                categories = bookService.getAllCategories();
                System.out.println("Categories loaded: " + (categories != null ? categories.size() : "null"));
            } catch (Exception e) {
                System.err.println("Error loading categories: " + e.getMessage());
                e.printStackTrace();
            }
            
            model.addAttribute("books", books);
            model.addAttribute("search", search);
            model.addAttribute("selectedCategory", category);
            model.addAttribute("categories", categories);
            model.addAttribute("currentUser", currentUser);
            
            System.out.println("=== RETURNING TO TEMPLATE ===");
            return "student/browse";
            
        } catch (Exception e) {
            System.err.println("FATAL ERROR in browse controller: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("books", new java.util.ArrayList<>());
            model.addAttribute("categories", new java.util.ArrayList<>());
            model.addAttribute("error", "Unable to load books: " + e.getMessage());
            return "student/browse";
        }
    }
    
    @PostMapping("/borrow/{bookId}")
    public String borrowBook(@PathVariable Long bookId, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser();
            Optional<Book> bookOpt = bookService.findById(bookId);
            
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/student/browse";
            }
            
            if (!bookOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Book not found!");
                return "redirect:/student/browse";
            }
            
            borrowService.borrowBook(currentUser, bookOpt.get());
            redirectAttributes.addFlashAttribute("success", "Book borrowed successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/student/browse";
    }
    
    @PostMapping("/return/{bookId}")
    public String returnBook(@PathVariable Long bookId, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser();
            Optional<Book> bookOpt = bookService.findById(bookId);
            
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/student/my-books";
            }
            
            if (!bookOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Book not found!");
                return "redirect:/student/my-books";
            }
            
            borrowService.returnBook(currentUser, bookOpt.get());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving review: " + e.getMessage());
        }

        return "redirect:/student/my-books";
    }

    @PostMapping("/review/{bookId}")
    public String createOrUpdateReview(@PathVariable Long bookId,
                                     @RequestParam Integer rating,
                                     @RequestParam(required = false) String reviewText,
                                     RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser();
            Optional<Book> bookOpt = bookService.findById(bookId);

            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/student/my-books";
            }

            if (!bookOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Book not found!");
                return "redirect:/student/my-books";
            }

            // Validate rating
            if (rating < 1 || rating > 5) {
                redirectAttributes.addFlashAttribute("error", "Rating must be between 1 and 5!");
                return "redirect:/student/my-books";
            }

            bookReviewService.addOrUpdateReview(currentUser, bookOpt.get(), rating, reviewText);
            redirectAttributes.addFlashAttribute("success", "Review saved successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving review: " + e.getMessage());
        }

        return "redirect:/student/my-books";
    }

    @PostMapping("/review/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser();

            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/student/my-books";
            }

            Optional<BookReview> reviewOpt = bookReviewService.findById(reviewId);

            if (!reviewOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Review not found!");
                return "redirect:/student/my-books";
            }

            BookReview review = reviewOpt.get();

            // Check if the review belongs to the current user
            if (!review.getUser().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "You can only delete your own reviews!");
                return "redirect:/student/my-books";
            }

            bookReviewService.deleteReview(reviewId);
            redirectAttributes.addFlashAttribute("success", "Review deleted successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting review: " + e.getMessage());
        }

        return "redirect:/student/my-books";
    }
    public String myBooks(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            List<BorrowRecord> borrowedBooks = borrowService.getUserBorrowedBooks(currentUser);
            List<BorrowRecord> borrowHistory = borrowService.getUserBorrowHistory(currentUser);
            List<BookReview> reviews = bookReviewService.getReviewsByUser(currentUser);
            
            model.addAttribute("borrowedBooks", borrowedBooks);
            model.addAttribute("borrowHistory", borrowHistory);
            model.addAttribute("user", currentUser);
        }
        
        return "student/my-books";
    }
    
    @GetMapping("/history")
    public String history(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            List<BorrowRecord> borrowHistory = borrowService.getUserBorrowHistory(currentUser);
            
            model.addAttribute("borrowHistory", borrowHistory);
            model.addAttribute("user", currentUser);
        }
        
        return "student/history";
    }
    
    @GetMapping("/favorites")
    public String favorites(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            // For now, we'll show recently borrowed books as "favorites"
            // In a real app, you'd have a favorites table
            List<BorrowRecord> recentBooks = borrowService.getUserBorrowHistory(currentUser);
            
            model.addAttribute("favoriteBooks", recentBooks);
            model.addAttribute("user", currentUser);
        }
        
        return "student/favorites";
    }
    
    @GetMapping("/profile")
    public String profile(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            List<BorrowRecord> borrowedBooks = borrowService.getUserBorrowedBooks(currentUser);
            List<BorrowRecord> borrowHistory = borrowService.getUserBorrowHistory(currentUser);
            
            model.addAttribute("user", currentUser);
            model.addAttribute("totalBorrowed", borrowHistory.size());
            model.addAttribute("currentlyBorrowed", borrowedBooks.size());
            model.addAttribute("availableSlots", 5 - borrowedBooks.size());
        }
        
        return "student/profile";
    }
    
    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        try {
            System.out.println("=== EDIT PROFILE CONTROLLER CALLED ===");
            User currentUser = getCurrentUser();
            System.out.println("Current user: " + (currentUser != null ? currentUser.getUsername() : "null"));
            
            model.addAttribute("user", currentUser);
            
            System.out.println("=== RETURNING TO edit-profile TEMPLATE ===");
            return "student/edit-profile";
        } catch (Exception e) {
            System.err.println("Error in edit profile controller: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Unable to load edit profile page: " + e.getMessage());
            return "student/profile";
        }
    }
    
    @GetMapping("/profile/change-password")
    public String changePassword(Model model) {
        try {
            System.out.println("=== CHANGE PASSWORD CONTROLLER CALLED ===");
            User currentUser = getCurrentUser();
            System.out.println("Current user: " + (currentUser != null ? currentUser.getUsername() : "null"));
            
            model.addAttribute("user", currentUser);
            
            System.out.println("=== RETURNING TO change-password TEMPLATE ===");
            return "student/change-password";
        } catch (Exception e) {
            System.err.println("Error in change password controller: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Unable to load change password page: " + e.getMessage());
            return "student/profile";
        }
    }
    
    @GetMapping("/profile/notifications")
    public String notifications(Model model) {
        try {
            System.out.println("=== NOTIFICATIONS CONTROLLER CALLED ===");
            User currentUser = getCurrentUser();
            System.out.println("Current user: " + (currentUser != null ? currentUser.getUsername() : "null"));
            
            model.addAttribute("user", currentUser);
            model.addAttribute("notifications", new java.util.ArrayList<>());
            
            System.out.println("=== RETURNING TO notifications TEMPLATE ===");
            return "student/notifications";
        } catch (Exception e) {
            System.err.println("Error in notifications controller: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Unable to load notifications page: " + e.getMessage());
            return "student/profile";
        }
    }
}
