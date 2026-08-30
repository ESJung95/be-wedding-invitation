package com.wedding.invitation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "message", schema = "wedding-invitation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(name = "guest_name", length = 50)
    private String guestName;

    @Column(name = "access_type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private AccessType accessType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Message(Guest guest, String guestName, AccessType accessType, String content) {
        this.guest = guest;
        this.guestName = guestName;
        this.accessType = accessType;
        this.content = content;
    }
}