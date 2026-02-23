package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public List<User> findAllStudents() {
        return userRepository.findByRole(User.Role.STUDENT);
    }
    
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public User createDefaultLibrarian() {
        if (!existsByUsername("librarian")) {
            User librarian = new User();
            librarian.setUsername("librarian");
            librarian.setPassword("password");
            librarian.setFullName("Default Librarian");
            librarian.setEmail("librarian@nandana.edu");
            librarian.setRole(User.Role.LIBRARIAN);
            return saveUser(librarian);
        }
        return findByUsername("librarian").orElse(null);
    }
    
    public User createDefaultStudent() {
        if (!existsByUsername("student")) {
            User student = new User();
            student.setUsername("student");
            student.setPassword("password");
            student.setFullName("Default Student");
            student.setEmail("student@nandana.edu");
            student.setRole(User.Role.STUDENT);
            return saveUser(student);
        }
        return findByUsername("student").orElse(null);
    }
    
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    public User updateUserProfile(User user) {
        // Don't encode password if it's not being changed
        return userRepository.save(user);
    }
}
