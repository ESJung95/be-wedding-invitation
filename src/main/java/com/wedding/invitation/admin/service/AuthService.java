package com.wedding.invitation.admin.service;

import com.wedding.invitation.admin.dto.LoginRequest;
import com.wedding.invitation.admin.dto.LoginResponse;
import com.wedding.invitation.admin.repository.AdminRepository;
import com.wedding.invitation.common.config.jwt.JwtTokenProvider;
import com.wedding.invitation.common.exception.CustomException;
import com.wedding.invitation.common.exception.ErrorCode;
import com.wedding.invitation.domain.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            // Same error as "username not found" on purpose, so the response never
            // reveals whether the username or the password was wrong.
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(admin.getId(), admin.getUsername());

        return new LoginResponse(accessToken, admin.getId(), admin.getUsername());
    }
}