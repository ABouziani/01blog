package com.example.blog.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.blog.exceptions.RefreshTokenExpiredException;
import com.example.blog.exceptions.TokenManagementException;
import com.example.blog.model.RefreshToken;
import com.example.blog.model.User;
import com.example.blog.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenDurationMs;

    // single constructor
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.refreshExpirationMs}") Long refreshTokenDurationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenDurationMs = refreshTokenDurationMs;
    }

    public RefreshToken createRefreshToken(User user) {
        try {
            refreshTokenRepository.deleteByExpiryDateBefore(OffsetDateTime.now());
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setExpiryDate(OffsetDateTime.now().plusSeconds(refreshTokenDurationMs));
            refreshToken.setToken(UUID.randomUUID().toString());
            return refreshTokenRepository.save(refreshToken);
        } catch (DataIntegrityViolationException e) {
            System.out.println(e.getMessage());
            throw new TokenManagementException("Failed to create refresh token for user: " + user.getId());
        }
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(OffsetDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException(token.getToken());
        }
        return token;
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
