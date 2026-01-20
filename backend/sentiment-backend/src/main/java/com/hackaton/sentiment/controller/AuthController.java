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
 * Controlador de autenticación y gestión de usuarios.
 *
 * Proporciona endpoints para el registro, inicio de sesión, gestión de tokens JWT
 * y operaciones relacionadas con la autenticación de usuarios en el sistema.
 *
 * Todos los endpoints están disponibles sin autenticación excepto aquellos que
 * requieren un token válido para acceder a información del usuario actual.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@SecurityRequirement(name = "Bearer Authentication")//linea agregada para el candado en swager
@RequiredArgsConstructor
@Tag(name = "Autentificacion", description = "Endpoints de autentificacion")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Autentica a un usuario y genera un token JWT de acceso.
     *
     * Verifica las credenciales del usuario (username y password) y, si son válidas,
     * genera un token JWT que puede ser utilizado para acceder a endpoints protegidos.
     *
     * @param request DTO con las credenciales de autenticación
     * @return ResponseEntity con el token JWT y datos del usuario o mensaje de error
     */
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario mediante sus credenciales y genera un token JWT que permitirá el acceso seguro a los recursos protegidos de la API."
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

            return ResponseEntity.ok(new AuthResponseDTO(token, "Bearer", user.getUsername(), user.getRole().name()));

        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * Crea una nueva cuenta de usuario con rol USER por defecto. Valida que el
     * username y email no estén previamente registrados. Retorna un token JWT
     * inmediatamente después del registro exitoso.
     *
     * @param request DTO con los datos de registro del usuario
     * @return ResponseEntity con el token JWT y datos del usuario o mensaje de error
     */
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario en la plataforma utilizando los datos de registro proporcionados. " +
                    "Al completarse correctamente, el usuario podrá iniciar sesión en el sistema."
    )

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

    /**
     * Obtiene la información del usuario actualmente autenticado.
     *
     * Extrae el token JWT del encabezado Authorization y retorna los datos
     * del perfil del usuario sin incluir información sensible como la contraseña.
     *
     * @param authHeader Encabezado Authorization con el token Bearer
     * @return ResponseEntity con los datos del perfil del usuario o mensaje de error
     */
    @Operation(
            summary = "Obtener usuario autenticado",
            description = "Retorna la información básica del usuario actualmente autenticado, " +
                    "obtenida a partir del token JWT enviado en la solicitud."
    )
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

    /**
     * Genera un nuevo token de acceso a partir de un token de actualización.
     *
     * Permite renovar el token de acceso sin requerir nuevas credenciales,
     * manteniendo la sesión activa. Valida que el token de actualización no haya expirado.
     *
     * @param authHeader Encabezado Authorization con el token de actualización
     * @return ResponseEntity con el nuevo token JWT o mensaje de error
     */
    @Operation(
            summary = "Refrescar token JWT",
            description = "Genera un nuevo token de acceso a partir de un token de actualización válido, " +
                    "permitiendo mantener la sesión activa sin necesidad de volver a iniciar sesión."
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
            return ResponseEntity.ok(new AuthResponseDTO(newToken, "Bearer", user.getUsername(), user.getRole().name()));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
    }

    /**
     * Inicia el proceso de recuperación de contraseña.
     *
     * Endpoint inicial para el proceso de recuperación de contraseña.
     * En una implementación completa, enviaría un email con instrucciones
     * para restablecer la contraseña.
     *
     * @param email Email del usuario que desea recuperar la contraseña
     * @return ResponseEntity con mensaje informativo
     */
    @Operation(
            summary = "Recuperar contraseña",
            description = "Permite restablecer la contraseña de un usuario que no ha iniciado sesión, " +
                    "validando su identidad mediante los datos proporcionados y generando una nueva credencial segura."
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        // Lógica para enviar email con link de recuperación
        return ResponseEntity.ok("Si el email existe, recibirás instrucciones");
    }

    /**
     * Valida la vigencia y autenticidad de un token JWT.
     *
     * Verifica si el token proporcionado es válido, no ha expirado
     * y pertenece a un usuario autorizado en el sistema.
     *
     * @param authHeader Encabezado Authorization con el token a validar
     * @return ResponseEntity con el resultado de la validación
     */
    @Operation(
            summary = "Validar token JWT",
            description = "Verifica si un token JWT es válido, no ha expirado y pertenece a un usuario autorizado para acceder a la API."
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