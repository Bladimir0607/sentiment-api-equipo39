package com.hackaton.sentiment.controller;

import com.hackaton.sentiment.dto.UserProfileDTO;
import com.hackaton.sentiment.dto.request.ChangePasswordRequestDTO;
import com.hackaton.sentiment.dto.request.UpdateProfileRequestDTO;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión del perfil de usuario.
 *
 * <p>Proporciona endpoints que permiten a los usuarios autenticados
 * consultar y modificar su información personal, cambiar su contraseña
 * y eliminar su cuenta del sistema.</p>
 *
 * <p>Todos los endpoints requieren una autenticación JWT válida y solo
 * permiten operar sobre la información del usuario actualmente autenticado.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 *
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
     * <p>Extrae el nombre de usuario desde el {@link SecurityContextHolder}
     * manejando los distintos tipos posibles de principal, como
     * {@link UserDetails}, {@link String} u otros objetos de autenticación.</p>
     *
     * @return nombre de usuario autenticado
     * @throws RuntimeException si no existe un usuario autenticado en el contexto
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
        }

        return authentication.getName();
    }

    /**
     * Obtiene el perfil del usuario autenticado.
     *
     * <p>Recupera la información del usuario desde la base de datos y
     * retorna un DTO sin incluir datos sensibles como la contraseña.</p>
     *
     * @return {@link ResponseEntity} con {@link UserProfileDTO}
     * @throws RuntimeException si el usuario no existe en la base de datos
     */
    @Operation(
            summary = "Obtener perfil del usuario autenticado",
            description = "Retorna la información completa del perfil del usuario que ha iniciado sesión."
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
     * <p>Permite modificar campos específicos como nombre completo y correo
     * electrónico. Válida que el email no esté ya registrado por otro usuario.</p>
     *
     * @param request DTO con los datos a actualizar
     * @return {@link ResponseEntity} con mensaje de confirmación o error
     */
    @Operation(
            summary = "Actualizar perfil del usuario",
            description = "Permite al usuario autenticado modificar su información personal."
    )
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody UpdateProfileRequestDTO request) {

        String username = getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

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
     * <p>Verifica que la contraseña actual sea válida antes de realizar
     * la actualización, garantizando la seguridad del proceso.</p>
     *
     * @param request DTO con la contraseña actual y la nueva contraseña
     * @return {@link ResponseEntity} con mensaje de confirmación o error
     */
    @Operation(
            summary = "Cambiar contraseña del usuario",
            description = "Permite al usuario autenticado actualizar su contraseña."
    )
    @PostMapping("/me/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO request) {

        String username = getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Contraseña actual incorrecta");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Usuario {} cambió su contraseña", username);
        return ResponseEntity.ok("Contraseña cambiada exitosamente");
    }

    /**
     * Elimina la cuenta del usuario autenticado.
     *
     * <p>Elimina permanentemente la cuenta del usuario junto con toda
     * la información asociada, incluyendo sus análisis de sentimiento.</p>
     *
     * @return {@link ResponseEntity} con mensaje de confirmación
     */
    @Operation(
            summary = "Eliminar cuenta propia",
            description = "Elimina permanentemente la cuenta del usuario autenticado."
    )
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount() {
        String username = getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        sentimentService.deleteAnalysesByUser(user);
        userRepository.delete(user);

        log.warn("Usuario {} eliminó su cuenta", username);
        return ResponseEntity.ok("Cuenta eliminada exitosamente");
    }

    /*
     * =========================================================================
     * ENDPOINT ADMINISTRATIVO (DESHABILITADO)
     * =========================================================================
     *
     * Endpoint administrativo para obtener el historial de análisis de un usuario
     * específico. Actualmente, se encuentra comentado y su funcionalidad está
     * cubierta por el controlador de administración.
     *
     * @Operation(
     *     summary = "Obtener análisis de sentimiento de un usuario",
     *     description = "Retorna el historial de análisis de un usuario específico."
     * )
     *
     * @GetMapping("/{userId}/analyses")
     * @PreAuthorize("hasRole('ADMIN')")
     */
}