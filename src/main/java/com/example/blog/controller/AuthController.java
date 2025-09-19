package com.example.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.TokenRefreshRequest;
import com.example.blog.exceptions.RefreshTokenNotFoundException;
import com.example.blog.model.JwtResponse;
import com.example.blog.model.RefreshToken;
import com.example.blog.model.User;
import com.example.blog.service.JwtUtil;
import com.example.blog.service.RefreshTokenService;
import com.example.blog.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService,
            PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    // ------------------- LOGIN -------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // This will use your CustomUserDetailsService → returns UserDetailsImpl
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword())
        );

        // Save it in context — now it contains UserDetailsImpl
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("🔍 Principal type AFTER login: " + authentication.getPrincipal().getClass().getName());
        System.out.println("🔍 Principal content: " + authentication.getPrincipal().toString());

        // Fetch user for JWT (optional — you could get username from authentication)
        User user = userService.findByUsernameOrEmail(request.getIdentifier())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken()));
    }

    // ------------------- REFRESH TOKEN -------------------
    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        System.out.println(request);
        String token = request.getRefreshToken();

        return refreshTokenService.findByToken(token)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newJwt = jwtUtil.generateToken(user.getUsername(), user.getRole());
                    return ResponseEntity.ok(new JwtResponse(newJwt, token));
                })
                .orElseThrow(() -> new RefreshTokenNotFoundException(token)); // ← triggers global handler
    }
}
