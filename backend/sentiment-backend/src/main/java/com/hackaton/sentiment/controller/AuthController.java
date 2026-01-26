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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST responsable de la autenticación y gestión básica de usuarios.
 *
 * <p>Proporciona endpoints para el registro de usuarios, inicio de sesión,
 * generación y validación de tokens JWT, así como la obtención de información
 * del usuario autenticado.</p>
 *
 * <p>Los endpoints públicos permiten el acceso sin autenticación, mientras que
 * las operaciones relacionadas con el usuario actual requieren un token JWT válido.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 *
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de autenticación y gestión de usuarios")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Auténtica a un usuario y genera un token JWT de acceso.
     *
     * <p>Válida las credenciales proporcionadas (nombre de usuario y contraseña).
     * Si la autenticación es exitosa, se genera un token JWT que permite acceder
     * a los endpoints protegidos de la API.</p>
     *
     * @param request objeto que contiene las credenciales de autenticación
     * @return {@link ResponseEntity} con el token JWT y datos básicos del usuario,
     *         o un mensaje de error en caso de credenciales inválidas
     */
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario mediante sus credenciales y genera un token JWT para el acceso seguro a la API."
    )
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

            return ResponseEntity.ok(
                    new AuthResponseDTO(
                            token,
                            "Bearer",
                            user.getUsername(),
                            user.getRole().name()
                    )
            );

        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * <p>Crea una cuenta con rol {@code USER} por defecto, validando que el
     * nombre de usuario y el correo electrónico no estén previamente registrados.
     * Tras un registro exitoso, se genera automáticamente un token JWT.</p>
     *
     * @param request objeto con los datos de registro del usuario
     * @return {@link ResponseEntity} con el token JWT generado o un mensaje de error
     */
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario y genera un token JWT al completar el registro exitosamente."
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            log.info("Registration attempt for user: {}", request.getEmail());

            if (userRepository.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest().body("Username already exists");
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body("Email already registered");
            }

            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .fullName(request.getFullName())
                    .role(UserRole.USER)
                    .enabled(true)
                    .build();

            userRepository.save(user);

            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(
                    new AuthResponseDTO(
                            token,
                            "Bearer",
                            user.getUsername(),
                            user.getRole().name()
                    )
            );

        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Obtiene la información del usuario actualmente autenticado.
     *
     * <p>Extrae el token JWT del encabezado {@code Authorization} y retorna
     * información básica del perfil del usuario, excluyendo datos sensibles
     * como la contraseña.</p>
     *
     * @param authHeader encabezado Authorization con el token Bearer
     * @return {@link ResponseEntity} con los datos del perfil del usuario
     *         o un mensaje de error si el token es inválido
     */
    @Operation(
            summary = "Obtener usuario autenticado",
            description = "Retorna la información básica del usuario actualmente autenticado mediante el token JWT."
    )
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(
                    UserProfileDTO.builder()
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .fullName(user.getFullName())
                            .role(user.getRole().name())
                            .createdAt(user.getCreatedAt())
                            .updatedAt(user.getUpdatedAt())
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }

    /**
     * Genera un nuevo token de acceso a partir de un token existente.
     *
     * <p>Permite renovar el token JWT sin necesidad de volver a autenticarse,
     * siempre que el token proporcionado sea válido y no haya expirado.</p>
     *
     * @param authHeader encabezado Authorization con el token Bearer
     * @return {@link ResponseEntity} con un nuevo token JWT o mensaje de error
     */
    @Operation(
            summary = "Refrescar token JWT",
            description = "Genera un nuevo token de acceso a partir de un token válido, manteniendo la sesión activa."
    )
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

            return ResponseEntity.ok(
                    new AuthResponseDTO(
                            newToken,
                            "Bearer",
                            user.getUsername(),
                            user.getRole().name()
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
    }

    /**
     * Inicia el proceso de recuperación de contraseña.
     *
     * <p>Este endpoint representa el punto de entrada para la recuperación
     * de contraseña. En una implementación completa, enviaría un correo
     * electrónico con instrucciones para restablecerla.</p>
     *
     * @param email correo electrónico del usuario
     * @return {@link ResponseEntity} con un mensaje informativo
     */
    @Operation(
            summary = "Recuperar contraseña",
            description = "Inicia el proceso de recuperación de contraseña para un usuario registrado."
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        return ResponseEntity.ok("Si el email existe, recibirás instrucciones");
    }

    /**
     * Válida la vigencia y autenticidad de un token JWT.
     *
     * <p>Comprueba si el token proporcionado es válido, no ha expirado
     * y pertenece a un usuario autorizado dentro del sistema.</p>
     *
     * @param authHeader encabezado Authorization con el token Bearer
     * @return {@link ResponseEntity} indicando si el token es válido
     */
    @Operation(
            summary = "Validar token JWT",
            description = "Verifica si un token JWT es válido y se encuentra vigente."
    )
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
