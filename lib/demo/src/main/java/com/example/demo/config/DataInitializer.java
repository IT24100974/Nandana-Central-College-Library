package com.example.demo.config;

import com.example.demo.service.BookService;
import com.example.demo.service.UserService;
import com.example.demo.service.LibraryRuleService;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private LibraryRuleService libraryRuleService;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize library rules first
        libraryRuleService.initializeDefaultRules();
        
        // Create default users for all roles
        createDefaultUsers();
        
        // Create sample books
        bookService.createSampleBooks();
        
        System.out.println("=== Enhanced Library Management System Initialized ===");
        System.out.println("System Admin - Username: admin, Password: password");
        System.out.println("Librarian - Username: librarian, Password: password");
        System.out.println("Library Assistant - Username: assistant, Password: password");
        System.out.println("Deputy Principal - Username: deputy, Password: password");
        System.out.println("Student - Username: student, Password: password");
        System.out.println("Access the application at: http://localhost:8080");
        System.out.println("========================================================");
    }
    
    private void createDefaultUsers() {
        // Create System Admin
        if (!userService.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("password");
            admin.setFullName("System Administrator");
            admin.setEmail("admin@nandana.edu");
            admin.setRole(User.Role.SYSTEM_ADMIN);
            userService.saveUser(admin);
        }
        
        // Create Librarian
        userService.createDefaultLibrarian();
        
        // Create Library Assistant
        if (!userService.existsByUsername("assistant")) {
            User assistant = new User();
            assistant.setUsername("assistant");
            assistant.setPassword("password");
            assistant.setFullName("Library Assistant");
            assistant.setEmail("assistant@nandana.edu");
            assistant.setRole(User.Role.LIBRARY_ASSISTANT);
            userService.saveUser(assistant);
        }
        
        // Create Deputy Principal
        if (!userService.existsByUsername("deputy")) {
            User deputy = new User();
            deputy.setUsername("deputy");
            deputy.setPassword("password");
            deputy.setFullName("Deputy Principal");
            deputy.setEmail("deputy@nandana.edu");
            deputy.setRole(User.Role.DEPUTY_PRINCIPAL);
            userService.saveUser(deputy);
        }
        
        // Create Student
        userService.createDefaultStudent();
    }
}
