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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private LibraryRuleService libraryRuleService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BorrowService borrowService;
    
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName()).orElse(null);
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<User> allUsers = userService.findAllUsers();
        List<Book> allBooks = bookService.findAllBooks();
        List<BorrowRecord> borrowedBooks = borrowService.getAllBorrowedBooks();
        List<BorrowRecord> overdueBooks = borrowService.getOverdueBooks();
        List<Notification> activeNotifications = notificationService.getActiveNotifications();
        
        // User statistics by role
        long studentCount = allUsers.stream().filter(u -> u.getRole() == User.Role.STUDENT).count();
        long librarianCount = allUsers.stream().filter(u -> u.getRole() == User.Role.LIBRARIAN).count();
        long assistantCount = allUsers.stream().filter(u -> u.getRole() == User.Role.LIBRARY_ASSISTANT).count();
        
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("studentCount", studentCount);
        model.addAttribute("librarianCount", librarianCount);
        model.addAttribute("assistantCount", assistantCount);
        model.addAttribute("totalBooks", allBooks.size());
        model.addAttribute("borrowedBooksCount", borrowedBooks.size());
        model.addAttribute("overdueCount", overdueBooks.size());
        model.addAttribute("activeNotificationsCount", activeNotifications.size());
        model.addAttribute("recentUsers", allUsers.stream().limit(5).toList());
        model.addAttribute("recentNotifications", activeNotifications.stream().limit(5).toList());
        
        return "admin/dashboard-modern";
    }
    
    // User Management
    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    @GetMapping("/users/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", User.Role.values());
        return "admin/add-user";
    }
    
    @PostMapping("/users/add")
    public String addUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            if (userService.existsByUsername(user.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "Username already exists!");
                return "redirect:/admin/users/add";
            }
            
            if (userService.existsByEmail(user.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Email already exists!");
                return "redirect:/admin/users/add";
            }
            
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "User created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("roles", User.Role.values());
            return "admin/edit-user";
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/admin/users";
        }
    }
    
    @PostMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> existingUser = userService.findById(id);
            if (existingUser.isPresent()) {
                User userToUpdate = existingUser.get();
                userToUpdate.setFullName(user.getFullName());
                userToUpdate.setEmail(user.getEmail());
                userToUpdate.setRole(user.getRole());
                userToUpdate.setEnabled(user.isEnabled());
                
                userService.saveUser(userToUpdate);
                redirectAttributes.addFlashAttribute("success", "User updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    // Notification Management
    @GetMapping("/notifications")
    public String manageNotifications(Model model) {
        List<Notification> notifications = notificationService.getAllNotifications();
        model.addAttribute("notifications", notifications);
        return "admin/notifications";
    }
    
    @GetMapping("/notifications/add")
    public String addNotificationForm(Model model) {
        model.addAttribute("notification", new Notification());
        model.addAttribute("types", Notification.NotificationType.values());
        model.addAttribute("priorities", Notification.Priority.values());
        return "admin/add-notification";
    }
    
    @PostMapping("/notifications/add")
    public String addNotification(@ModelAttribute Notification notification, 
                                @RequestParam(required = false) String expiryDate,
                                RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser();
            notification.setCreatedBy(currentUser);
            
            if (expiryDate != null && !expiryDate.trim().isEmpty()) {
                notification.setExpiresAt(LocalDateTime.parse(expiryDate + "T23:59:59"));
            }
            
            notificationService.saveNotification(notification);
            redirectAttributes.addFlashAttribute("success", "Notification created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating notification: " + e.getMessage());
        }
        return "redirect:/admin/notifications";
    }
    
    @PostMapping("/notifications/deactivate/{id}")
    public String deactivateNotification(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            notificationService.deactivateNotification(id);
            redirectAttributes.addFlashAttribute("success", "Notification deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deactivating notification: " + e.getMessage());
        }
        return "redirect:/admin/notifications";
    }

    @GetMapping("/notifications/edit/{id}")
    public String editNotificationForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Notification> notification = notificationService.findById(id);
        if (notification.isPresent()) {
            model.addAttribute("notification", notification.get());
            model.addAttribute("types", Notification.NotificationType.values());
            model.addAttribute("priorities", Notification.Priority.values());
            return "admin/edit-notification";
        } else {
            redirectAttributes.addFlashAttribute("error", "Notification not found!");
            return "redirect:/admin/notifications";
        }
    }

    @PostMapping("/notifications/edit/{id}")
    public String editNotification(@PathVariable Long id,
                                 @ModelAttribute Notification notification,
                                 @RequestParam(required = false) String expiryDate,
                                 RedirectAttributes redirectAttributes) {
        try {
            Optional<Notification> existingNotificationOpt = notificationService.findById(id);
            if (existingNotificationOpt.isPresent()) {
                Notification existingNotification = existingNotificationOpt.get();

                // Update fields
                existingNotification.setTitle(notification.getTitle());
                existingNotification.setMessage(notification.getMessage());
                existingNotification.setType(notification.getType());
                existingNotification.setPriority(notification.getPriority());

                // Handle expiry date
                if (expiryDate != null && !expiryDate.trim().isEmpty()) {
                    existingNotification.setExpiresAt(LocalDateTime.parse(expiryDate + "T23:59:59"));
                } else {
                    existingNotification.setExpiresAt(null);
                }

                notificationService.saveNotification(existingNotification);
                redirectAttributes.addFlashAttribute("success", "Notification updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Notification not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating notification: " + e.getMessage());
        }
        return "redirect:/admin/notifications";
    }

    @PostMapping("/notifications/delete/{id}")
    public String deleteNotification(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            notificationService.deleteNotification(id);
            redirectAttributes.addFlashAttribute("success", "Notification deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting notification: " + e.getMessage());
        }
        return "redirect:/admin/notifications";
    }

    @GetMapping("/rules")
    public String manageRules(Model model) {
        List<LibraryRule> rules = libraryRuleService.getAllRules();
        model.addAttribute("rules", rules);
        model.addAttribute("borrowLimit", libraryRuleService.getBorrowLimit());
        model.addAttribute("loanDuration", libraryRuleService.getLoanDurationDays());
        model.addAttribute("fineRate", libraryRuleService.getFineRatePerDay());
        model.addAttribute("reservationLimit", libraryRuleService.getReservationLimit());
        model.addAttribute("renewalLimit", libraryRuleService.getRenewalLimit());
        return "admin/rules";
    }
    
    @PostMapping("/rules/update")
    public String updateRules(@RequestParam int borrowLimit,
                            @RequestParam int loanDuration,
                            @RequestParam BigDecimal fineRate,
                            @RequestParam int reservationLimit,
                            @RequestParam int renewalLimit,
                            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser();
            String updatedBy = currentUser != null ? currentUser.getUsername() : "admin";
            
            libraryRuleService.setBorrowLimit(borrowLimit, updatedBy);
            libraryRuleService.setLoanDurationDays(loanDuration, updatedBy);
            libraryRuleService.setFineRatePerDay(fineRate, updatedBy);
            libraryRuleService.setReservationLimit(reservationLimit, updatedBy);
            libraryRuleService.setRenewalLimit(renewalLimit, updatedBy);
            
            redirectAttributes.addFlashAttribute("success", "Library rules updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating rules: " + e.getMessage());
        }
        return "redirect:/admin/rules";
    }
    
    @PostMapping("/rules/delete/{id}")
    public String deleteRule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            libraryRuleService.deleteRule(id);
            redirectAttributes.addFlashAttribute("success", "Rule deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting rule: " + e.getMessage());
        }
        return "redirect:/admin/rules";
    }
    @PostMapping("/rules/create")
    public String createRule(@RequestParam String ruleName,
                           @RequestParam String ruleValue,
                           @RequestParam String ruleType,
                           @RequestParam(required = false) String description,
                           RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser();
            String updatedBy = currentUser != null ? currentUser.getUsername() : "admin";

            // Check if rule already exists
            if (libraryRuleService.findByRuleName(ruleName).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Rule with name '" + ruleName + "' already exists!");
                return "redirect:/admin/rules";
            }

            LibraryRule.RuleType type;
            try {
                type = LibraryRule.RuleType.valueOf(ruleType);
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("error", "Invalid rule type: " + ruleType);
                return "redirect:/admin/rules";
            }

            LibraryRule newRule = new LibraryRule(ruleName, ruleValue, description, type);
            newRule.setUpdatedBy(updatedBy);
            libraryRuleService.saveRule(newRule);

            redirectAttributes.addFlashAttribute("success", "Rule created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating rule: " + e.getMessage());
        }
        return "redirect:/admin/rules";
    }
    public String systemReports(Model model) {
        List<User> allUsers = userService.findAllUsers();
        List<Book> allBooks = bookService.findAllBooks();
        List<BorrowRecord> allBorrows = borrowService.getAllBorrowedBooks();
        List<BorrowRecord> overdueBooks = borrowService.getOverdueBooks();
        
        // Statistics
        long studentCount = allUsers.stream().filter(u -> u.getRole() == User.Role.STUDENT).count();
        long librarianCount = allUsers.stream().filter(u -> u.getRole() == User.Role.LIBRARIAN).count();
        long assistantCount = allUsers.stream().filter(u -> u.getRole() == User.Role.LIBRARY_ASSISTANT).count();
        
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("studentCount", studentCount);
        model.addAttribute("librarianCount", librarianCount);
        model.addAttribute("assistantCount", assistantCount);
        model.addAttribute("totalBooks", allBooks.size());
        model.addAttribute("borrowedBooksCount", allBorrows.size());
        model.addAttribute("overdueCount", overdueBooks.size());
        model.addAttribute("availableBooks", allBooks.stream().mapToInt(Book::getAvailableCopies).sum());
        
        return "admin/reports";
    }
    
    // Test endpoint
    @GetMapping("/test")
    public String test() {
        System.out.println("=== TEST ENDPOINT CALLED ===");
        return "admin/dashboard";
    }
    
    // Test update endpoint
    @PostMapping("/test-update")
    public String testUpdate(@RequestParam String testParam, RedirectAttributes redirectAttributes) {
        System.out.println("=== TEST UPDATE ENDPOINT CALLED ===");
        System.out.println("Test Param: " + testParam);
        redirectAttributes.addFlashAttribute("success", "Test update successful: " + testParam);
        return "redirect:/admin/profile";
    }
    
    // Admin Profile
    @GetMapping("/profile")
    public String adminProfile(Model model) {
        System.out.println("=== ADMIN PROFILE CONTROLLER CALLED ===");
        
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        System.out.println("Current user: " + currentUser.getUsername());
        
        // Add basic user info
        model.addAttribute("user", currentUser);
        model.addAttribute("totalUsersManaged", 0);
        model.addAttribute("totalNotifications", 0);
        model.addAttribute("activeNotifications", 0);
        model.addAttribute("totalBooks", 0);
        model.addAttribute("activeBorrows", 0);
        model.addAttribute("studentCount", 0);
        model.addAttribute("librarianCount", 0);
        model.addAttribute("assistantCount", 0);
        
        System.out.println("=== RETURNING TO admin/profile TEMPLATE ===");
        return "admin/profile";
    }
    
    // Admin Profile Edit
    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        System.out.println("=== ADMIN EDIT PROFILE CONTROLLER CALLED ===");
        
        try {
            User currentUser = getCurrentUser();
            if (currentUser != null) {
                System.out.println("Current user: " + currentUser.getUsername());
                model.addAttribute("user", currentUser);
            } else {
                System.out.println("No current user found");
                // Create a dummy user for testing
                User dummyUser = new User();
                dummyUser.setUsername("admin");
                dummyUser.setFullName("System Administrator");
                dummyUser.setEmail("admin@library.com");
                model.addAttribute("user", dummyUser);
            }
        } catch (Exception e) {
            System.err.println("Error getting user: " + e.getMessage());
            // Create a dummy user for testing
            User dummyUser = new User();
            dummyUser.setUsername("admin");
            dummyUser.setFullName("System Administrator");
            dummyUser.setEmail("admin@library.com");
            model.addAttribute("user", dummyUser);
        }
        
        System.out.println("=== RETURNING TO admin/edit-profile-simple TEMPLATE ===");
        return "admin/edit-profile-simple";
    }
    
    // Update Admin Profile
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String fullName, 
                              @RequestParam String email, 
                              RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== UPDATE PROFILE CONTROLLER CALLED ===");
            System.out.println("Full Name: " + fullName);
            System.out.println("Email: " + email);
            
            // Validate input
            if (fullName == null || fullName.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Full name is required!");
                return "redirect:/admin/profile/edit";
            }
            
            if (email == null || email.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email is required!");
                return "redirect:/admin/profile/edit";
            }
            
            try {
                User currentUser = getCurrentUser();
                if (currentUser != null) {
                    // Update only allowed fields
                    currentUser.setFullName(fullName.trim());
                    currentUser.setEmail(email.trim());
                    
                    userService.updateUserProfile(currentUser);
                    redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
                    System.out.println("=== PROFILE UPDATE SUCCESSFUL ===");
                } else {
                    redirectAttributes.addFlashAttribute("error", "User session not found. Please login again.");
                    return "redirect:/login";
                }
            } catch (Exception userEx) {
                System.err.println("Error with user operations: " + userEx.getMessage());
                redirectAttributes.addFlashAttribute("error", "Unable to update profile. Please try again.");
                return "redirect:/admin/profile/edit";
            }
            
        } catch (Exception e) {
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
            return "redirect:/admin/profile/edit";
        }
        
        return "redirect:/admin/profile";
    }
    
    // Admin Change Password
    @GetMapping("/profile/change-password")
    public String changePassword(Model model) {
        try {
            System.out.println("=== ADMIN CHANGE PASSWORD CONTROLLER CALLED ===");
            User currentUser = getCurrentUser();
            System.out.println("Current user: " + (currentUser != null ? currentUser.getUsername() : "null"));
            
            if (currentUser == null) {
                return "redirect:/login";
            }
            
            model.addAttribute("user", currentUser);
            
            System.out.println("=== RETURNING TO admin/change-password TEMPLATE ===");
            return "admin/change-password";
        } catch (Exception e) {
            System.err.println("Error in admin change password controller: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Unable to load change password page: " + e.getMessage());
            return "admin/profile";
        }
    }
    
    // Update Admin Password
    @PostMapping("/profile/change-password")
    public String updatePassword(@RequestParam String currentPassword,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/login";
            }
            
            // Validate new password
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "New passwords do not match!");
                return "redirect:/admin/profile/change-password";
            }
            
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters long!");
                return "redirect:/admin/profile/change-password";
            }
            
            // Update password through user service
            userService.updatePassword(currentUser, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error changing password: " + e.getMessage());
            return "redirect:/admin/profile/change-password";
        }
        
        return "redirect:/admin/profile";
    }
}
