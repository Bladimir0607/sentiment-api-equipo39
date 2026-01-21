package com.hackaton.sentiment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO que representa el perfil de un usuario autenticado.
 *
 * <p>Este objeto se utiliza para transferir información del perfil del usuario
 * desde el backend hacia el frontend, excluyendo datos sensibles como la
 * contraseña.</p>
 *
 * <p>Se emplea principalmente en endpoints relacionados con la consulta del
 * perfil del usuario actual (por ejemplo: {@code GET /api/users/me}).</p>
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Data
@Builder
public class UserProfileDTO {

    /**
     * Nombre de usuario único del sistema.
     */
    private String username;

    /**
     * Correo electrónico asociado a la cuenta del usuario.
     */
    private String email;

    /**
     * Nombre completo del usuario.
     *
     * <p>Campo opcional que permite mostrar información más amigable
     * en la interfaz de usuario.</p>
     */
    private String fullName;

    /**
     * Rol del usuario dentro del sistema.
     *
     * <p>Ejemplos comunes: {@code ADMIN}, {@code USER}.</p>
     */
    private String role;

    /**
     * Fecha y hora de creación de la cuenta del usuario.
     */
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del perfil del usuario.
     */
    private LocalDateTime updatedAt;
}