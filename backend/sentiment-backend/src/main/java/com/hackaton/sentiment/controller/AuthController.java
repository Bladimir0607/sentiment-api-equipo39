package com.hackaton.sentiment.controller;

import com.hackaton.sentiment.dto.UserProfileDTO;
import com.hackaton.sentiment.dto.request.RegisterRequestDTO;
import com.hackaton.sentiment.entity.User;
import com.hackaton.sentiment.entity.UserRole;
import com.hackaton.sentiment.repository.UserRepository;
import com.hackaton.sentiment.dto.request.AuthRequestDTO;
import com.hackaton.sentiment.dto.response.AuthResponseDTO;
import com.hackaton.sentiment.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@SecurityRequirement(name = "Bearer Authentication")//linea agregada para el candado en swager
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        try {
            log.info("Login attempt for user: {}", request.getUsername());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(new AuthResponseDTO(token, "Bearer", user.getUsername(), user.getRole().name()));

        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            log.info("Registration attempt for user: {}", request.getEmail());

            // Verificar si usuario ya existe
            if (userRepository.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest().body("Username already exists");
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body("Email already registered");
            }

            // Crear nuevo usuario
            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .fullName(request.getFullName())
                    .role(UserRole.USER)
                    .enabled(true)
                    .build();

            userRepository.save(user);

            // Generar token automáticamente
            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(new AuthResponseDTO(token, "Bearer", user.getUsername(), user.getRole().name()));

        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Retornar datos del usuario sin password
            return ResponseEntity.ok(UserProfileDTO.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole().name())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }
    @Operation(summary = "Refrescar token JWT")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String refreshToken = authHeader.substring(7);
            if (jwtService.isTokenExpired(refreshToken)) {
                return ResponseEntity.status(401).body("Token expirado");
            }

            String username = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String newToken = jwtService.generateToken(user);
            return ResponseEntity.ok(new AuthResponseDTO(newToken, "Bearer", user.getUsername(), user.getRole().name()));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
    }

    @Operation(summary = "Cambiar contraseña (sin estar logueado - recuperación)")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        // Lógica para enviar email con link de recuperación
        return ResponseEntity.ok("Si el email existe, recibirás instrucciones");
    }

    @Operation(summary = "Validar token JWT")
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            boolean isValid = jwtService.validateToken(token);
            return ResponseEntity.ok(Map.of("valid", isValid));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
}