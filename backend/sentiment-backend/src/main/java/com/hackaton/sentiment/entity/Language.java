package com.hackaton.sentiment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un idioma disponible en el sistema.
 *
 * <p>Esta entidad se utiliza para gestionar los idiomas soportados por la
 * plataforma, especialmente en el módulo de internacionalización (i18n).
 * Permite definir qué idiomas están activos, cuál es el idioma por defecto
 * y almacenar información adicional como el nombre nativo.</p>
 *
 * <p>La información se persiste en la tabla {@code languages}.</p>
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Entity
@Table(name = "languages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Language {

    /**
     * Código único del idioma.
     *
     * <p>Generalmente, sigue el estándar ISO 639-1 (ej.: {@code es}, {@code en},
     * {@code pt}). Actúa como clave primaria de la entidad.</p>
     */
    @Id
    @Column(length = 5)
    private String code;

    /**
     * Nombre del idioma en inglés.
     *
     * <p>Ejemplo: {@code Spanish}, {@code English}, {@code Portuguese}.</p>
     */
    @Column(nullable = false)
    private String name;

    /**
     * Nombre del idioma en su forma nativa.
     *
     * <p>Ejemplo: {@code Español}, {@code English}, {@code Português}.</p>
     */
    @Column(name = "native_name")
    private String nativeName;

    /**
     * Indica si el idioma está activo en el sistema.
     *
     * <p>Solo los idiomas activos pueden ser utilizados para traducciones
     * y selección en el frontend.</p>
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Indica si este idioma es el idioma por defecto del sistema.
     *
     * <p>El idioma por defecto se utiliza cuando no se especifica un idioma
     * explícitamente en las solicitudes.</p>
     */
    @Column(name = "is_default")
    private Boolean isDefault = false;

    /**
     * Fecha y hora de creación del registro del idioma.
     *
     * <p>Este campo se asigna automáticamente al persistir la entidad
     * y no es actualizable.</p>
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Método callback ejecutado antes de persistir la entidad.
     *
     * <p>Asigna automáticamente la fecha y hora actuales al campo
     * {@code createdAt}.</p>
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
