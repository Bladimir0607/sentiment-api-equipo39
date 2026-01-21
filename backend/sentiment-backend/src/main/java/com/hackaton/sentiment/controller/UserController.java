package com.hackaton.sentiment.controller;

import com.hackaton.sentiment.dto.UserProfileDTO;
import com.hackaton.sentiment.dto.request.ChangePasswordRequestDTO;
import com.hackaton.sentiment.dto.request.UpdateProfileRequestDTO;
import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.entity.User;
import com.hackaton.sentiment.repository.UserRepository;
import com.hackaton.sentiment.service.JwtService;
import com.hackaton.sentiment.service.SentimentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión del perfil de usuario.
 *
 * Proporciona endpoints para que los usuarios autenticados gestionen su propia información,
 * incluyendo consulta y actualización de perfil, cambio de contraseña y eliminación de cuenta.
 *
 * Todos los endpoints requieren autenticación JWT válida y solo permiten al usuario
 * acceder y modificar su propia información.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Gestión del perfil de usuario")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final SentimentService sentimentService;

    /**
     * Obtiene el nombre de usuario del contexto de seguridad actual.
     *
     * Extrae el nombre de usuario del objeto de autenticación almacenado en el
     * SecurityContextHolder de Spring Security, manejando diferentes tipos de
     * objetos principal (UserDetails, String, etc.).
     *
     * @return Nombre de usuario del usuario autenticado
     * @throws RuntimeException si no hay usuario autenticado en el contexto
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("No hay usuario autenticado en el contexto actual");
            throw new RuntimeException("No hay usuario autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        } else {
            // Si es otro tipo de objeto (como Jwt en OAuth2) intentamos obtener el nombre
            return authentication.getName();
        }
    }

    /**
     * Obtiene el perfil completo del usuario autenticado.
     *
     * Recupera toda la información del perfil del usuario actualmente autenticado
     * desde la base de datos, excluyendo datos sensibles como la contraseña.
     *
     * @return ResponseEntity con el {@link UserProfileDTO} del usuario
     * @throws RuntimeException si el usuario no se encuentra en la base de datos
     */
    @Operation(
            summary = "Obtener perfil del usuario autenticado",
            description = "Retorna la información completa del perfil del usuario que ha iniciado sesión," +
                    " incluyendo sus datos personales registrados en la plataforma."
    )
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(UserProfileDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build());
    }

    /**
     * Actualiza la información del perfil del usuario autenticado.
     *
     * <p>Permite modificar campos específicos del perfil como nombre completo y email.
     * Valida que el nuevo email no esté ya registrado por otro usuario.</p>
     *
     * @param request DTO con los campos a actualizar
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @Operation(
            summary = "Actualizar perfil del usuario",
            description = "Permite al usuario autenticado modificar su información personal almacenada en la plataforma," +
                    " como nombre, correo electrónico u otros datos de perfil."
    )
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequestDTO request) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar campos permitidos
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body("El email ya está registrado");
            }
            user.setEmail(request.getEmail());
        }

        userRepository.save(user);
        return ResponseEntity.ok("Perfil actualizado correctamente");
    }

    /**
     * Cambia la contraseña del usuario autenticado.
     *
     * Verifica que la contraseña actual sea correcta antes de actualizarla por
     * una nueva, garantizando la seguridad del proceso de cambio de credenciales.
     *
     * @param request DTO con la contraseña actual y la nueva contraseña
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @Operation(
            summary = "Cambiar contraseña del usuario",
            description = "Permite al usuario autenticado actualizar su contraseña actual por una nueva," +
                    " garantizando la seguridad de su cuenta."
    )
    @PostMapping("/me/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Contraseña actual incorrecta");
        }

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Usuario {} cambió su contraseña", username);
        return ResponseEntity.ok("Contraseña cambiada exitosamente");
    }

    /**
     * Elimina la cuenta del usuario autenticado y toda su información asociada.
     *
     * Realiza una eliminación completa de la cuenta del usuario, incluyendo
     * opcionalmente todos los análisis de sentimiento asociados antes de eliminar
     * el registro del usuario.
     *
     * @return ResponseEntity con mensaje de confirmación
     */
    @Operation(
            summary = "Eliminar cuenta propia",
            description = "Elimina permanentemente la cuenta del usuario autenticado junto con su información asociada en el sistema."
    )
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Opcional: eliminar análisis asociados primero
        sentimentService.deleteAnalysesByUser(user);

        userRepository.delete(user);

        log.warn("Usuario {} eliminó su cuenta", username);
        return ResponseEntity.ok("Cuenta eliminada exitosamente");
    }

//    @Operation(
//            summary = "Obtener análisis de sentimiento de un usuario",
//            description = "Retorna el historial de análisis de sentimiento realizados por un usuario específico, identificado por su ID. Este endpoint es útil para consultas administrativas o análisis de comportamiento."
//    )
//    @GetMapping("/{userId}/analyses")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> getUserAnalyses(@PathVariable Long userId) {
//
//        log.info("🔍 ADMIN: Solicitando análisis del usuario ID: {}", userId);
//
//        // Verificar que el usuario existe
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
//
//        List<SentimentAnalysis> analyses = sentimentService.getUserAnalyses(userId);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("userId", userId);
//        response.put("username", user.getUsername());
//        response.put("totalAnalyses", analyses.size());
//        response.put("analyses", analyses.stream()
//                .map(analysis -> Map.of(
//                        "id", analysis.getId(),
//                        "text", analysis.getText().length() > 50 ?
//                                analysis.getText().substring(0, 50) + "..." : analysis.getText(),
//                        "sentiment", analysis.getLabel(),
//                        "probability", analysis.getProbability(),
//                        "createdAt", analysis.getCreatedAt()
//                ))
//                .collect(Collectors.toList()));
//
//        log.info("ADMIN: Encontrados {} análisis para el usuario {}",
//                analyses.size(), user.getUsername());
//
//        return ResponseEntity.ok(response);
//    }
}
