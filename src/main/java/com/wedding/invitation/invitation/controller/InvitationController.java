package com.wedding.invitation.invitation.controller;

import com.wedding.invitation.common.response.ApiResponse;
import com.wedding.invitation.domain.AccessType;
import com.wedding.invitation.invitation.dto.InvitationResponse;
import com.wedding.invitation.invitation.service.InvitationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invitation")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @GetMapping
    public ApiResponse<InvitationResponse> getInvitation(
            @RequestParam(required = false) String token,
            @RequestParam AccessType accessType,
            HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();
        InvitationResponse response = invitationService.getInvitation(token, accessType, ipAddress);
        return ApiResponse.success(response);
    }
}