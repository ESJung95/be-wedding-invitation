package com.wedding.invitation.admin.dto;

import com.wedding.invitation.domain.AccessType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageResponse {

    private final Long id;
    private final String guestName;
    private final AccessType accessType;
    private final String content;
    private final LocalDateTime createdAt;

    public MessageResponse(Long id, String guestName, AccessType accessType, String content, LocalDateTime createdAt) {
        this.id = id;
        this.guestName = guestName;
        this.accessType = accessType;
        this.content = content;
        this.createdAt = createdAt;
    }
}