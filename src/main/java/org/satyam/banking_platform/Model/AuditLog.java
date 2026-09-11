package org.satyam.banking_platform.Model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class AuditLog {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private AuditAction action;

        @Column(nullable = false)
        private String description;

        @Column(nullable = false)
        private LocalDateTime timestamp;

        private String ipAddress;

        @PrePersist
        public void onCreate() {
            timestamp = LocalDateTime.now();
        }
    }

