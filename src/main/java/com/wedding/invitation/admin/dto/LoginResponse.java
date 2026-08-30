package com.wedding.invitation.admin.dto;

public class LoginResponse {

    private final String accessToken;
    private final Long adminId;
    private final String username;

    public LoginResponse(String accessToken, Long adminId, String username) {
        this.accessToken = accessToken;
        this.adminId = adminId;
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getAdminId() {
        return adminId;
    }

    public String getUsername() {
        return username;
    }
}