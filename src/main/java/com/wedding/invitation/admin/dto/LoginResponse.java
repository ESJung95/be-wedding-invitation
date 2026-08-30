package com.wedding.invitation.admin.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private final String accessToken;
    private final Long adminId;
    private final String username;

    public LoginResponse(String accessToken, Long adminId, String username) {
        this.accessToken = accessToken;
        this.adminId = adminId;
        this.username = username;
    }
}