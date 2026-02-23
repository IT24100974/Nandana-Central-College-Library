package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User user = userService.findByUsername(auth.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
                switch (user.getRole()) {
                    case SYSTEM_ADMIN:
                        return "redirect:/admin/dashboard";
                    case LIBRARIAN:
                    case LIBRARY_ASSISTANT:
                        return "redirect:/librarian/dashboard";
                    case DEPUTY_PRINCIPAL:
                        return "redirect:/deputy/dashboard";
                    case STUDENT:
                    default:
                        return "redirect:/student/dashboard";
                }
            }
        }
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
