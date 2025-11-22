package com.example.blog.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.TokenRefreshRequest;
import com.example.blog.dtoresponses.EntityDtoMapper;
import com.example.blog.exceptions.RefreshTokenNotFoundException;
import com.example.blog.model.JwtResponse;
import com.example.blog.model.RefreshToken;
import com.example.blog.model.User;
import com.example.blog.service.JwtUtil;
import com.example.blog.service.RefreshTokenService;
import com.example.blog.service.UserDetailsImpl;
import com.example.blog.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4200")
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

        // Fetch user for JWT (optional — you could get username from authentication)
        Optional<User> userOpt = userService.findByUsernameOrEmail(request.getIdentifier());
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();

        if (user.isBanned()) {
            return new ResponseEntity<>("You are banned!", HttpStatus.FORBIDDEN);
        }

        String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), user.getRole().equals("ADMIN")));
    }

    //Check login
    @GetMapping("/islogged")
    public ResponseEntity<?> isLogged(HttpServletRequest request, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);

        }

        String token = authHeader.substring(7);

        if (jwtUtil.validateToken(token)) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long loggedUserId = userDetails.getId();
            Optional<User> loggedUserOpt = userService.findById(loggedUserId);
            if (loggedUserOpt.isEmpty()) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            User loggedUser = loggedUserOpt.get();
            if (loggedUser.isBanned()) {
                return ResponseEntity.status(401).body("User BANNED!");
            }
            return ResponseEntity.ok(EntityDtoMapper.toUserDto(loggedUser));
        } else {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
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
                    return ResponseEntity.ok(new JwtResponse(newJwt, token, user.getRole().equals("ADMIN")));
                })
                .orElseThrow(() -> new RefreshTokenNotFoundException(token)); 
    }
}
