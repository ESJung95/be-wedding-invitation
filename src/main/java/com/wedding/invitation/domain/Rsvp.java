package com.wedding.invitation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "rsvp",
        schema = "wedding-invitation",
        uniqueConstraints = @UniqueConstraint(name = "uq_rsvp_guest", columnNames = "guest_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rsvp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Rsvp(Guest guest, Status status) {
        this.guest = guest;
        this.status = status;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        ATTENDING, ABSENT
    }
}