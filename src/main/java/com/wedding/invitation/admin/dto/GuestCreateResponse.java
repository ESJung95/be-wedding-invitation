package com.wedding.invitation.admin.dto;

import com.wedding.invitation.domain.Guest;

import java.time.LocalDateTime;

public class GuestCreateResponse {

    private final Long id;
    private final String name;
    private final Guest.Side side;
    private final String token;
    private final boolean isActive;
    private final LocalDateTime createdAt;

    public GuestCreateResponse(Long id, String name, Guest.Side side, String token, boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.side = side;
        this.token = token;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Guest.Side getSide() {
        return side;
    }

    public String getToken() {
        return token;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}