package com.wedding.invitation.admin.dto;

import com.wedding.invitation.domain.AccessType;

import java.time.LocalDateTime;
import java.util.List;

public class RecentActivityResponse {

    private final List<RecentView> recentViews;
    private final List<RecentMessage> recentMessages;

    public RecentActivityResponse(List<RecentView> recentViews, List<RecentMessage> recentMessages) {
        this.recentViews = recentViews;
        this.recentMessages = recentMessages;
    }

    public List<RecentView> getRecentViews() {
        return recentViews;
    }

    public List<RecentMessage> getRecentMessages() {
        return recentMessages;
    }

    public static class RecentView {
        private final String guestName;
        private final AccessType accessType;
        private final LocalDateTime viewedAt;

        public RecentView(String guestName, AccessType accessType, LocalDateTime viewedAt) {
            this.guestName = guestName;
            this.accessType = accessType;
            this.viewedAt = viewedAt;
        }

        public String getGuestName() {
            return guestName;
        }

        public AccessType getAccessType() {
            return accessType;
        }

        public LocalDateTime getViewedAt() {
            return viewedAt;
        }
    }

    public static class RecentMessage {
        private final String guestName;
        private final AccessType accessType;
        private final String content;
        private final LocalDateTime createdAt;

        public RecentMessage(String guestName, AccessType accessType, String content, LocalDateTime createdAt) {
            this.guestName = guestName;
            this.accessType = accessType;
            this.content = content;
            this.createdAt = createdAt;
        }

        public String getGuestName() {
            return guestName;
        }

        public AccessType getAccessType() {
            return accessType;
        }

        public String getContent() {
            return content;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
}