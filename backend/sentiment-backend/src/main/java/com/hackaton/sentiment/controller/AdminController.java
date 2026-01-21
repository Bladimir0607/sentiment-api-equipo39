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
 * Controlador REST responsable de las operaciones administrativas del sistema.
 *
 * <p>Este controlador expone endpoints exclusivos para usuarios con rol
 * {@code ADMIN}, permitiendo la gestión de usuarios, auditoría de análisis
 * de sentimientos y consulta de estadísticas globales.</p>
 *
 * <p>Todos los endpoints requieren autenticación mediante JWT y autorización
 * basada en roles.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 *
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
     * <p>Retorna información resumida de cada usuario incluyendo:
     * identificador, nombre de usuario, correo electrónico, rol asignado
     * y fechas de creación y última actualización.</p>
     *
     * @return {@link ResponseEntity} con una lista de {@link UserResponseDTO}
     *         y estado HTTP {@code 200 OK}
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
     * Obtiene estadísticas generales relacionadas con los usuarios del sistema.
     *
     * <p>Las métricas devueltas incluyen:</p>
     * <ul>
     *   <li>Total de usuarios registrados</li>
     *   <li>Cantidad de administradores</li>
     *   <li>Cantidad de usuarios estándar</li>
     *   <li>Porcentaje de administradores</li>
     *   <li>Marca de tiempo de la consulta</li>
     * </ul>
     *
     * @return {@link ResponseEntity} con un mapa de estadísticas y estado HTTP {@code 200 OK}
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

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsuarios", totalUsers);
        stats.put("administradores", adminCount);
        stats.put("usuariosNormales", userCount);
        stats.put("porcentajeAdmins", totalUsers > 0
                ? String.format("%.1f%%", (adminCount * 100.0 / totalUsers))
                : "0%");
        stats.put("timestamp", java.time.LocalDateTime.now());

        log.info("ADMIN: Estadísticas generadas - Total: {}, Admins: {}", totalUsers, adminCount);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtiene todos los análisis de sentimiento realizados en el sistema.
     *
     * <p>Incluye información detallada del análisis junto con datos básicos
     * del usuario que lo ejecutó, permitiendo auditoría global.</p>
     *
     * @return {@link ResponseEntity} con una lista de análisis y estado HTTP {@code 200 OK}
     */
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
     * <p>Las métricas son delegadas al servicio de análisis de sentimientos
     * e incluyen información agregada de uso y comportamiento.</p>
     *
     * @return {@link ResponseEntity} con estadísticas avanzadas y estado HTTP {@code 200 OK}
     */
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
     * Elimina un usuario del sistema utilizando su identificador.
     *
     * <p>Validaciones aplicadas:</p>
     * <ul>
     *   <li>El usuario debe existir</li>
     *   <li>Un administrador no puede eliminar su propia cuenta</li>
     * </ul>
     *
     * @param userId identificador del usuario a eliminar
     * @return {@link ResponseEntity} con mensaje de confirmación o error
     */
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario por ID (solo ADMIN)"
    )
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {

        log.warn("ADMIN: Intentando eliminar usuario ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        String currentUsername = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Usuario actual no encontrado"));

        if (currentUser.getId().equals(userId)) {
            return ResponseEntity.badRequest().body("No puedes eliminarte a ti mismo");
        }

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
     * <p>Este endpoint permite auditar la actividad de análisis de cualquier
     * usuario del sistema, mostrando un resumen de cada análisis realizado.</p>
     *
     * @param userId identificador del usuario a consultar
     * @return {@link ResponseEntity} con información del usuario y su historial de análisis
     */
    @Operation(
            summary = "Obtener análisis de sentimiento de un usuario (Administrador)",
            description = "Permite a un administrador consultar el historial de análisis de cualquier usuario"
    )
    @GetMapping("/users/{userId}/analyses")
    public ResponseEntity<?> getUserAnalyses(@PathVariable Long userId) {

        log.info("ADMIN: Solicitando análisis del usuario ID: {}", userId);

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
                        "text", analysis.getText().length() > 50
                                ? analysis.getText().substring(0, 50) + "..."
                                : analysis.getText(),
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
