package com.urbanshopper.domain.notification;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import java.time.Instant;
import java.util.UUID;

/**
 * NotificationTemplate — message content per event type, bilingual (J-006).
 * Templates support variables like {shopper_name}, {order_number} for dynamic content.
 * Read-only reference entity.
 */
@Entity
@Table(name = "notification_templates")
@Immutable
@Getter
public class NotificationTemplate {

    @Id
    private UUID id;

    @Column(name = "template_key", length = 100, nullable = false, unique = true)
    private String templateKey;

    @Column(length = 20, nullable = false)
    private String channel;

    @Column(name = "title_en", length = 200)
    private String titleEn;

    @Column(name = "title_sw", length = 200)
    private String titleSw;

    @Column(name = "body_en", nullable = false, columnDefinition = "TEXT")
    private String bodyEn;

    @Column(name = "body_sw", nullable = false, columnDefinition = "TEXT")
    private String bodySw;

    @Column(length = 500)
    private String variables;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
