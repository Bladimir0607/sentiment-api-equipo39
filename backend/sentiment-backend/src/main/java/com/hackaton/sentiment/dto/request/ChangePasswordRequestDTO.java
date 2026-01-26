package com.hackaton.sentiment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para el cambio de contraseña de un usuario autenticado.
 *
 * <p>Este objeto transporta la contraseña actual del usuario y la nueva contraseña
 * que desea establecer. Es utilizado en el endpoint de cambio de contraseña y
 * cuenta con validaciones para garantizar la seguridad y consistencia de los datos.</p>
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Getter
@Setter
public class ChangePasswordRequestDTO {

    /**
     * Contraseña actual del usuario.
     *
     * <p>Debe coincidir con la contraseña almacenada en el sistema para permitir
     * el cambio por una nueva contraseña.</p>
     */
    @NotBlank(message = "La contraseña actual no puede estar vacía")
    private String currentPassword;

    /**
     * Nueva contraseña que el usuario desea establecer.
     *
     * <p>Debe cumplir con las políticas de seguridad mínimas definidas por el sistema,
     * incluyendo una longitud mínima de 6 caracteres.</p>
     */
    @NotBlank(message = "La nueva contraseña no puede estar vacía")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String newPassword;
}