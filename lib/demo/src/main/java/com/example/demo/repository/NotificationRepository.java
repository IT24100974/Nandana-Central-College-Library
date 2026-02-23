package com.example.demo.repository;

import com.example.demo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByActiveTrue();
    List<Notification> findByActiveTrueOrderByPriorityDescCreatedAtDesc();
    
    @Query("SELECT n FROM Notification n WHERE n.active = true AND (n.expiresAt IS NULL OR n.expiresAt > CURRENT_TIMESTAMP) ORDER BY n.priority DESC, n.createdAt DESC")
    List<Notification> findActiveAndNotExpiredNotifications();
    
    List<Notification> findByType(Notification.NotificationType type);
    List<Notification> findByPriority(Notification.Priority priority);
}
