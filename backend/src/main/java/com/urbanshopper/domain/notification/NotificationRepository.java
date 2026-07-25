package com.urbanshopper.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipientIdAndRecipientTypeOrderByCreatedAtDesc(
        UUID recipientId, String recipientType);

    @Query("SELECT n FROM Notification n WHERE n.recipientId = :recipientId "
         + "AND n.recipientType = :recipientType AND n.isRead = false "
         + "ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByRecipient(UUID recipientId, String recipientType);

    long countByRecipientIdAndRecipientTypeAndIsReadFalse(UUID recipientId, String recipientType);
}
