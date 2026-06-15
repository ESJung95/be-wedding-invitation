package com.wedding.invitation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitation_view", schema = "wedding-invitation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvitationView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(nullable = false, updatable = false)
    private LocalDateTime viewedAt;

    @Column(length = 45)
    private String ipAddress;

    @PrePersist
    protected void onCreate() {
        this.viewedAt = LocalDateTime.now();
    }

    @Builder
    public InvitationView(Guest guest, String ipAddress) {
        this.guest = guest;
        this.ipAddress = ipAddress;
    }
}