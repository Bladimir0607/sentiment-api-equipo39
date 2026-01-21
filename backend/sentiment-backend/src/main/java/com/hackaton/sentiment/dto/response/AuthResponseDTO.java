package com.hackaton.sentiment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO de respuesta para procesos de autenticación.
 *
 * <p>Este objeto es retornado por el sistema luego de una autenticación exitosa.
 * Contiene la información necesaria para que el cliente pueda gestionar la sesión
 * del usuario, incluyendo el token JWT y los datos básicos de identidad.</p>
 *
 * <p>El token devuelto debe ser enviado en los siguientes requests utilizando
 * el encabezado <strong>Authorization</strong> con el formato:</p>
 *
 * <pre>
 * Authorization: Bearer {token}
 * </pre>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 *
 */
@Data
@AllArgsConstructor
public class AuthResponseDTO {

    /**
     * Token JWT generado tras una autenticación exitosa.
     *
     * <p>Este token es utilizado para autorizar el acceso a los endpoints protegidos
     * del sistema.</p>
     */
    private String token;

    /**
     * Tipo de autenticación utilizada.
     *
     * <p>Generalmente su valor es <strong>"Bearer"</strong>, indicando que el token
     * debe enviarse como un Bearer Token en el encabezado Authorization.</p>
     */
    private String type;

    /**
     * Nombre de usuario autenticado.
     *
     * <p>Identificador único del usuario dentro del sistema.</p>
     */
    private String username;

    /**
     * Rol asignado al usuario autenticado.
     *
     * <p>Define los permisos y nivel de acceso del usuario dentro de la aplicación,
     * por ejemplo: <code>USER</code> o <code>ADMIN</code>.</p>
     */
    private String role;
}