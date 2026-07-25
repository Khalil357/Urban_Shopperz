package com.urbanshopper.domain.notification;

import com.urbanshopper.shared.exception.ApiResponse;
import com.urbanshopper.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Notification API — in-app notification inbox.
 *
 * GET    /api/v1/notifications              — List user's notifications
 * GET    /api/v1/notifications/unread       — List unread notifications
 * GET    /api/v1/notifications/unread/count — Unread count badge
 * POST   /api/v1/notifications/{id}/read    — Mark notification as read
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotifications(
            @RequestHeader("Authorization") String auth) {
        var userId = extractUserId(auth);
        var role = extractRole(auth);
        var recipientType = "CUSTOMER".equals(role) ? "customer" : "shopper";
        return ResponseEntity.ok(
            ApiResponse.success(notificationService.getNotifications(userId, recipientType)));
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnread(
            @RequestHeader("Authorization") String auth) {
        var userId = extractUserId(auth);
        var role = extractRole(auth);
        var recipientType = "CUSTOMER".equals(role) ? "customer" : "shopper";
        return ResponseEntity.ok(
            ApiResponse.success(notificationService.getUnreadNotifications(userId, recipientType)));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(
            @RequestHeader("Authorization") String auth) {
        var userId = extractUserId(auth);
        var role = extractRole(auth);
        var recipientType = "CUSTOMER".equals(role) ? "customer" : "shopper";
        return ResponseEntity.ok(
            ApiResponse.success(notificationService.getUnreadCount(userId, recipientType)));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private UUID extractUserId(String authHeader) {
        var token = authHeader.substring(7);
        return UUID.fromString(jwtService.validateToken(token).getSubject());
    }

    private String extractRole(String authHeader) {
        var token = authHeader.substring(7);
        return jwtService.validateToken(token).get("role", String.class);
    }
}
