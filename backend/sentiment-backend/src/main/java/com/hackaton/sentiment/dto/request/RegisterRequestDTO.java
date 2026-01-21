// RegisterRequestDTO.java
package com.hackaton.sentiment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO utilizado para el registro de nuevos usuarios en la plataforma.
 *
 * <p>Este objeto encapsula la información necesaria para crear una cuenta de usuario,
 * incluyendo credenciales básicas y datos personales opcionales. Las validaciones
 * garantizan la integridad y seguridad de los datos proporcionados.</p>
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Data
public class RegisterRequestDTO {

    /**
     * Nombre de usuario del nuevo usuario.
     *
     * <p>Debe ser único dentro del sistema y tener una longitud mínima de 3 caracteres
     * y máxima de 50.</p>
     */
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;

    /**
     * Dirección de correo electrónico del usuario.
     *
     * <p>Debe tener un formato válido de email y no estar vacía. Este correo se utilizará
     * para notificaciones y procesos de recuperación de cuenta.</p>
     */
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El correo electrónico debe tener un formato válido")
    private String email;

    /**
     * Contraseña de la cuenta.
     *
     * <p>Debe cumplir con las políticas mínimas de seguridad, incluyendo una longitud
     * mínima de 6 caracteres.</p>
     */
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    /**
     * Nombre completo del usuario.
     *
     * <p>Campo opcional utilizado para mostrar información personal dentro de la
     * plataforma.</p>
     */
    private String fullName;
}