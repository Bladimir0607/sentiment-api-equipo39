package com.hackaton.sentiment.dto.request;

import lombok.Data;
/**
 * DTO utilizado para la autenticación de usuarios.
 *
 * <p>Este objeto transporta las credenciales necesarias para que un usuario
 * pueda autenticarse en el sistema y obtener un token JWT válido.</p>
 *
 * <p>Se utiliza principalmente en el endpoint de login dentro del flujo
 * de autenticación.</p>
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Data
public class AuthRequestDTO {

    /**
     * Nombre de usuario del usuario que intenta autenticarse.
     *
     * <p>Debe coincidir con un usuario registrado previamente en el sistema.</p>
     */
    private String username;

    /**
     * Contraseña del usuario.
     *
     * <p>La contraseña será validada contra el valor almacenado en la base de datos
     * utilizando un {@code PasswordEncoder}. No se almacena ni se devuelve en texto plano.</p>
     */
    private String password;
}