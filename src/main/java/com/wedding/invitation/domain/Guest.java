package com.wedding.invitation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "guest",
        schema = "wedding-invitation",
        uniqueConstraints = @UniqueConstraint(name = "uq_guest_token", columnNames = "token")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Side side;

    @Column(nullable = false, length = 36)
    private String token;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Guest(String name, Side side, String token) {
        this.name = name;
        this.side = side;
        this.token = token;
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public enum Side {
        GROOM, BRIDE
    }
}