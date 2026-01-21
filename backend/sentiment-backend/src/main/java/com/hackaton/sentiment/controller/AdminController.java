package com.hackaton.sentiment.controller;

import com.hackaton.sentiment.dto.response.UserResponseDTO;
import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.entity.User;
import com.hackaton.sentiment.entity.UserRole;
import com.hackaton.sentiment.repository.UserRepository;
import com.hackaton.sentiment.service.SentimentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para operaciones administrativas del sistema.
 *
 * Este controlador proporciona endpoints exclusivos para usuarios con rol de administrador,
 * permitiendo la gestión de usuarios, consulta de estadísticas y auditoría de análisis de sentimientos.
 *
 * Todos los endpoints requieren autenticación JWT y el rol ADMIN para su acceso.
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Administración", description = "Endpoints exclusivos para administradores")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final UserRepository userRepository;
    private final SentimentService sentimentService;

    /**
     * Obtiene la lista completa de usuarios registrados en el sistema.
     *
     * Este endpoint retorna información básica de todos los usuarios incluyendo:
     * ID, nombre de usuario, email, rol y fechas de creación/actualización.
     *
     * @return ResponseEntity con lista de {@link UserResponseDTO} y estado HTTP 200
     */
    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Solo accesible para usuarios con rol ADMIN"
    )
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("ADMIN: Solicitando lista de todos los usuarios");

        List<User> users = userRepository.findAll();

        List<UserResponseDTO> userDTOs = users.stream()
                .map(user -> UserResponseDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        log.info("ADMIN: Se encontraron {} usuarios", userDTOs.size());
        return ResponseEntity.ok(userDTOs);
    }


    /**
     * Obtiene estadísticas generales sobre los usuarios del sistema.
     *
     * Las estadísticas incluyen:
     *
     *   1. Total de usuarios registrados.
     *   2. Cantidad de administradores.
     *   3. Cantidad de usuarios normales.
     *   4. Porcentaje de administradores.
     *   5. Timestamp de la consulta.
     *
     * @return ResponseEntity con mapa de estadísticas y estado HTTP 200
     */
    @Operation(
            summary = "Obtener estadísticas de usuarios",
            description = "Estadísticas solo visibles para administradores"
    )
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserStats() {
        log.info("ADMIN: Solicitando estadísticas de usuarios");

        long totalUsers = userRepository.count();
        long adminCount = userRepository.countByRole(UserRole.ADMIN);
        long userCount = userRepository.countByRole(UserRole.USER);

        var stats = new java.util.HashMap<String, Object>();
        stats.put("totalUsuarios", totalUsers);
        stats.put("administradores", adminCount);
        stats.put("usuariosNormales", userCount);
        stats.put("porcentajeAdmins", totalUsers > 0 ?
                String.format("%.1f%%", (adminCount * 100.0 / totalUsers)) : "0%");
        stats.put("timestamp", java.time.LocalDateTime.now());

        log.info("ADMIN: Estadísticas generadas - Total: {}, Admins: {}", totalUsers, adminCount);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene todos los análisis de sentimiento realizados por todos los usuarios.
     *
     * Cada análisis incluye información completa del análisis y datos básicos
     * del usuario que lo realizó, permitiendo auditoría completa del sistema.
     *
     * @return ResponseEntity con lista de análisis detallados y estado HTTP 200
     */
    // ADMIN - ver todos los análisis
    @Operation(
            summary = "Ver todos los análisis",
            description = "Obtiene todos los análisis de todos los usuarios"
    )
    @GetMapping("/analyses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllAnalyses() {
        log.info("ADMIN: Solicitando todos los análisis");

        List<SentimentAnalysis> analyses = sentimentService.getAllAnalyses();

        List<Map<String, Object>> response = analyses.stream()
                .map(analysis -> Map.of(
                        "id", analysis.getId(),
                        "text", analysis.getText(),
                        "label", analysis.getLabel(),
                        "probability", analysis.getProbability(),
                        "user", Map.of(
                                "id", analysis.getUser().getId(),
                                "username", analysis.getUser().getUsername(),
                                "email", analysis.getUser().getEmail()
                        ),
                        "createdAt", analysis.getCreatedAt()
                ))
                .collect(Collectors.toList());

        log.info("ADMIN: Se encontraron {} análisis", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene estadísticas avanzadas del sistema.
     *
     * Delega la obtención de estadísticas detalladas al servicio de sentimientos,
     * que puede incluir métricas como análisis por fecha, distribución de sentimientos,
     * actividad de usuarios, entre otros.
     *
     * @return ResponseEntity con estadísticas avanzadas y estado HTTP 200
     */
    //ADMIN - estadísticas avanzadas
    @Operation(
            summary = "Estadísticas avanzadas",
            description = "Estadísticas detalladas del sistema"
    )
    @GetMapping("/advanced-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdvancedStats() {
        return ResponseEntity.ok(sentimentService.getAdvancedStats());
    }

    /**
     * Elimina un usuario del sistema por su ID.
     *
     * Validaciones realizadas:
     *
     *   1. El usuario debe existir.
     *   2. El administrador no puede eliminarse a sí mismo.
     *
     * @param userId ID del usuario a eliminar
     * @return ResponseEntity con mensaje de confirmación o error
     *
     * @throws RuntimeException si el usuario actual no se encuentra en el sistema
     */
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario por ID (solo ADMIN)"
    )
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        log.warn("ADMIN: Intentando eliminar usuario ID: {}", userId);

        // Verificar que el usuario existe
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        // Obtener usuario actual para verificar que no se esta eliminando a sí mismo
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Usuario actual no encontrado"));

        if (currentUser.getId().equals(userId)) {
            return ResponseEntity.badRequest().body("No puedes eliminarte a ti mismo");
        }

        // Eliminar el usuario
        userRepository.deleteById(userId);

        log.info("ADMIN: Usuario ID {} eliminado exitosamente", userId);
        return ResponseEntity.ok(Map.of(
                "message", "Usuario eliminado exitosamente",
                "deletedUserId", userId
        ));
    }

    /**
     * Obtiene el historial completo de análisis de sentimiento de un usuario específico.
     *
     * Este endpoint permite a los administradores auditar la actividad de análisis
     * de cualquier usuario, incluyendo un resumen de texto, sentimiento detectado,
     * probabilidad y fecha de cada análisis.
     *
     * @param userId ID del usuario cuyos análisis se desean consultar
     * @return ResponseEntity con información del usuario y su historial de análisis
     * @throws RuntimeException si el usuario no existe
     */
    @Operation(
            summary = "Obtener análisis de sentimiento de un usuario (Administrador)",
            description = "Permite a un administrador consultar el historial de análisis de sentimiento de cualquier usuario del sistema con fines de auditoría y análisis global."
    )
    @GetMapping("/users/{userId}/analyses")
    public ResponseEntity<?> getUserAnalyses(@PathVariable Long userId) {

        log.info("🔍 ADMIN: Solicitando análisis del usuario ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<SentimentAnalysis> analyses = sentimentService.getUserAnalyses(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("username", user.getUsername());
        response.put("totalAnalyses", analyses.size());
        response.put("analyses", analyses.stream()
                .map(analysis -> Map.of(
                        "id", analysis.getId(),
                        "text", analysis.getText().length() > 50 ?
                                analysis.getText().substring(0, 50) + "..." : analysis.getText(),
                        "sentiment", analysis.getLabel(),
                        "probability", analysis.getProbability(),
                        "createdAt", analysis.getCreatedAt()
                ))
                .collect(Collectors.toList()));

        log.info("ADMIN: Encontrados {} análisis para el usuario {}",
                analyses.size(), user.getUsername());

        return ResponseEntity.ok(response);
    }
}