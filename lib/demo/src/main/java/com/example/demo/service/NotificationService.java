package com.example.demo.service;

import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
    
    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }
    
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    public List<Notification> getActiveNotifications() {
        return notificationRepository.findActiveAndNotExpiredNotifications();
    }
    
    public List<Notification> getNotificationsByType(Notification.NotificationType type) {
        return notificationRepository.findByType(type);
    }
    
    public List<Notification> getNotificationsByPriority(Notification.Priority priority) {
        return notificationRepository.findByPriority(priority);
    }
    
    public Notification createNotification(String title, String message, 
                                         Notification.NotificationType type, 
                                         Notification.Priority priority, 
                                         User createdBy) {
        Notification notification = new Notification(title, message, type, priority, createdBy);
        return saveNotification(notification);
    }
    
    public Notification createNotificationWithExpiry(String title, String message, 
                                                   Notification.NotificationType type, 
                                                   Notification.Priority priority, 
                                                   User createdBy, 
                                                   LocalDateTime expiresAt) {
        Notification notification = new Notification(title, message, type, priority, createdBy);
        notification.setExpiresAt(expiresAt);
        return saveNotification(notification);
    }
    
    public void deactivateNotification(Long id) {
        Optional<Notification> notificationOpt = findById(id);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setActive(false);
            saveNotification(notification);
        }
    }
    
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
    
    public void cleanupExpiredNotifications() {
        List<Notification> activeNotifications = notificationRepository.findByActiveTrue();
        for (Notification notification : activeNotifications) {
            if (notification.isExpired()) {
                notification.setActive(false);
                saveNotification(notification);
            }
        }
    }
    
    // System notification methods
    public void createDueDateReminder(User user, String bookTitle, LocalDateTime dueDate) {
        String title = "Book Due Date Reminder";
        String message = String.format("Dear %s, your borrowed book '%s' is due on %s. Please return it on time to avoid fines.", 
                                     user.getFullName(), bookTitle, dueDate.toLocalDate());
        createNotification(title, message, Notification.NotificationType.REMINDER, 
                         Notification.Priority.MEDIUM, user);
    }
    
    public void createOverdueNotification(User user, String bookTitle, int daysOverdue) {
        String title = "Overdue Book Notice";
        String message = String.format("Dear %s, your borrowed book '%s' is %d days overdue. Please return it immediately to avoid additional fines.", 
                                     user.getFullName(), bookTitle, daysOverdue);
        createNotification(title, message, Notification.NotificationType.ALERT, 
                         Notification.Priority.HIGH, user);
    }
    
    public void createBookAvailableNotification(User user, String bookTitle) {
        String title = "Reserved Book Available";
        String message = String.format("Dear %s, your reserved book '%s' is now available for pickup. Please collect it within 7 days.", 
                                     user.getFullName(), bookTitle);
        createNotificationWithExpiry(title, message, Notification.NotificationType.REMINDER, 
                                   Notification.Priority.MEDIUM, user, LocalDateTime.now().plusDays(7));
    }
}
