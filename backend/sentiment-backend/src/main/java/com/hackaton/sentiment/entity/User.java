package com.hackaton.sentiment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entidad que representa a un usuario del sistema.
 *
 * <p>Esta clase implementa la interfaz {@link UserDetails} de Spring Security,
 * lo que permite integrar directamente la entidad {@code User} con el
 * mecanismo de autenticación y autorización basado en JWT.</p>
 *
 * <p>La información del usuario se almacena en la tabla {@code users} e incluye
 * credenciales, rol, estado de la cuenta y datos de auditoría.</p>
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    /**
     * Identificador único del usuario.
     *
     * <p>Se genera automáticamente mediante una estrategia de identidad.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario único del sistema.
     *
     * <p>Se utiliza como identificador principal para autenticación.</p>
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Correo electrónico del usuario.
     *
     * <p>Debe ser único y se utiliza para contacto y recuperación de cuenta.</p>
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Contraseña del usuario.
     *
     * <p>Se almacena en formato cifrado mediante un {@code PasswordEncoder}.</p>
     */
    @Column(nullable = false)
    private String password;

    /**
     * Nombre completo del usuario.
     *
     * <p>Campo opcional utilizado para mostrar información amigable en la interfaz.</p>
     */
    private String fullName;

    /**
     * Rol del usuario dentro del sistema.
     *
     * <p>Determina los permisos y niveles de acceso disponibles.</p>
     */
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * Indica si la cuenta del usuario está habilitada.
     *
     * <p>Si es {@code false}, el usuario no podrá autenticarse.</p>
     */
    private boolean enabled = true;

    /**
     * Fecha y hora de creación del usuario.
     *
     * <p>Se establece automáticamente al persistir la entidad.</p>
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del usuario.
     *
     * <p>Se actualiza automáticamente cada vez que se modifica la entidad.</p>
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Callback ejecutado antes de persistir la entidad.
     *
     * <p>Inicializa los campos {@code createdAt} y {@code updatedAt}
     * con la fecha y hora actuales.</p>
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Callback ejecutado antes de actualizar la entidad.
     *
     * <p>Actualiza automáticamente el campo {@code updatedAt}.</p>
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Retorna las autoridades (roles) asignadas al usuario.
     *
     * <p>Spring Security utiliza este método para evaluar permisos.
     * El rol se expone con el prefijo {@code ROLE_}.</p>
     *
     * @return colección de autoridades del usuario
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Indica si la cuenta del usuario no ha expirado.
     *
     * @return {@code true} si la cuenta es válida
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si la cuenta del usuario no está bloqueada.
     *
     * @return {@code true} si la cuenta no está bloqueada
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales del usuario no han expirado.
     *
     * @return {@code true} si las credenciales son válidas
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario está habilitado.
     *
     * <p>Este valor controla si el usuario puede autenticarse.</p>
     *
     * @return {@code true} si el usuario está activo
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}