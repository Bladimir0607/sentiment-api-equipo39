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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Administración", description = "Endpoints exclusivos para administradores")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final UserRepository userRepository;
    private final SentimentService sentimentService;

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

    //ADMIN - eliminar usuario
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
}