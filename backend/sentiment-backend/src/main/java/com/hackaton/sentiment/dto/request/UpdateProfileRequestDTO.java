package com.hackaton.sentiment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para actualizar la información del perfil del usuario.
 *
 * <p>Este objeto permite modificar de forma parcial los datos personales del usuario
 * autenticado. Todos los campos son opcionales; únicamente se actualizarán aquellos
 * que sean enviados en la solicitud.</p>
 *
 * <p>Las validaciones garantizan que los datos proporcionados cumplan con los
 * formatos y longitudes permitidas.</p>
 *
 *  * @author Equipo Hackathon Oracle ONE - Backend
 *  * @version 1.4
 *  * @since 2026-01-21
 *
 */
@Getter
@Setter
public class UpdateProfileRequestDTO {

    /**
     * Dirección de correo electrónico del usuario.
     *
     * <p>Debe tener un formato válido de correo electrónico. Si se envía, el sistema
     * verificará que no esté registrado previamente por otro usuario.</p>
     */
    @Email(message = "El correo electrónico debe tener un formato válido")
    private String email;

    /**
     * Nombre completo del usuario.
     *
     * <p>Debe tener una longitud mínima de 2 caracteres y máxima de 100. Este campo
     * es opcional y se utiliza únicamente con fines de visualización.</p>
     */
    @Size(
            min = 2,
            max = 100,
            message = "El nombre completo debe tener entre 2 y 100 caracteres"
    )
    private String fullName;
}