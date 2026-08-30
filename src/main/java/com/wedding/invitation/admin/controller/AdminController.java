package com.wedding.invitation.admin.controller;

import com.wedding.invitation.admin.dto.DashboardResponse;
import com.wedding.invitation.admin.dto.LoginRequest;
import com.wedding.invitation.admin.dto.LoginResponse;
import com.wedding.invitation.admin.service.AuthService;
import com.wedding.invitation.admin.service.DashboardService;
import com.wedding.invitation.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final DashboardService dashboardService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/dashboard")
    public ApiResponse<DashboardResponse> getDashboard() {
        DashboardResponse response = dashboardService.getDashboard();
        return ApiResponse.success(response);
    }
}