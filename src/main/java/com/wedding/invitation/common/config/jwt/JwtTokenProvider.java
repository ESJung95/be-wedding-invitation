package com.wedding.invitation.common.config.jwt;

import com.wedding.invitation.common.exception.CustomException;
import com.wedding.invitation.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * jwt.admin.secret must be at least 32 characters (256 bits) for HS256.
 * A short secret makes Keys.hmacShaKeyFor throw at startup.
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.admin.secret}") String secret,
            @Value("${jwt.admin.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.admin.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(Long adminId, String username) {
        return generateToken(adminId, username, accessTokenExpiration);
    }

    public String generateRefreshToken(Long adminId) {
        return generateToken(adminId, null, refreshTokenExpiration);
    }

    private String generateToken(Long adminId, String username, long expiration) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        JwtBuilder builder = Jwts.builder()
                .subject(String.valueOf(adminId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key);

        if (username != null) {
            builder.claim("username", username);
        }

        return builder.compact();
    }

    public Long getAdminId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    /**
     * Throws CustomException(EXPIRED_TOKEN) or CustomException(INVALID_TOKEN) on failure.
     * Callers that only want a yes/no answer should catch CustomException.
     */
    public void validateToken(String token) {
        parseClaims(token);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (SignatureException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
}