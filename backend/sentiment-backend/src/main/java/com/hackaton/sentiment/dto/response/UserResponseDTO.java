package com.hackaton.sentiment.dto.response;

import com.hackaton.sentiment.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para información de usuarios.
 *
 * <p>Este objeto se utiliza para exponer datos del usuario hacia el cliente,
 * asegurando que no se incluyan campos sensibles como contraseñas u otra
 * información confidencial.</p>
 *
 * <p>Es comúnmente utilizado en endpoints administrativos o de perfil de usuario
 * para mostrar información básica y metadatos asociados a la cuenta.</p>
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    /**
     * Identificador único del usuario.
     */
    private Long id;

    /**
     * Nombre de usuario.
     *
     * <p>Es el identificador principal utilizado para autenticación.</p>
     */
    private String username;

    /**
     * Dirección de correo electrónico del usuario.
     */
    private String email;

    /**
     * Rol asignado al usuario dentro del sistema.
     *
     * <p>Determina el nivel de acceso y permisos del usuario.</p>
     */
    private UserRole role;

    /**
     * Fecha y hora en que la cuenta fue creada.
     */
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del perfil del usuario.
     */
    private LocalDateTime updatedAt;

    /**
     * Obtiene una representación amigable del rol del usuario.
     *
     * <p>Este método es útil para mostrar el rol en interfaces de usuario
     * sin exponer directamente los valores técnicos del enum.</p>
     *
     * @return Nombre legible del rol o "No definido" si el rol es nulo
     */
    public String getRoleDisplay() {
        if (role == null) {
            return "No definido";
        }

        return switch (role) {
            case ADMIN -> "Administrador";
            case USER -> "Usuario";
            default -> role.name();
        };
    }
}