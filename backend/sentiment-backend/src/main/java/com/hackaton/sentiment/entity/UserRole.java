package com.hackaton.sentiment.entity;

/**
 * Enumeración que define los roles de usuario dentro del sistema.
 *
 * <p>Estos roles se utilizan principalmente para la gestión de permisos
 * y control de acceso mediante Spring Security. Cada rol es convertido
 * automáticamente en una autoridad con el prefijo {@code ROLE_}.</p>
 *
 * <p>Ejemplo de uso en Spring Security:
 * <pre>
 * new SimpleGrantedAuthority("ROLE_" + role.name())
 * </pre>
 * </p>
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
public enum UserRole {

    /**
     * Rol estándar asignado a los usuarios comunes del sistema.
     * <p>
     * Tiene acceso limitado a las funcionalidades básicas de la aplicación.
     * </p>
     */
    USER,

    /**
     * Rol con privilegios administrativos.
     * <p>
     * Tiene acceso completo a la configuración del sistema, gestión de usuarios
     * y operaciones críticas.
     * </p>
     */
    ADMIN,

    /**
     * Rol intermedio con permisos de moderación.
     * <p>
     * Puede gestionar contenido, revisar información y realizar acciones
     * de supervisión sin tener control total del sistema.
     * </p>
     */
    MODERATOR
}