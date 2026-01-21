package com.hackaton.sentiment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una traducción almacenada en el sistema.
 *
 * <p>Esta entidad se utiliza para persistir traducciones de textos identificados
 * por una clave lógica y un idioma específico. Las traducciones pueden provenir
 * de archivos {@code .properties}, base de datos o servicios externos como
 * LibreTranslate.</p>
 *
 * <p>La información se almacena en la tabla {@code translations} y permite
 * manejar versionado, auditoría temporal y relación con idiomas soportados.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Entity
@Table(name = "translations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Translation {

    /**
     * Identificador único de la traducción.
     *
     * <p>Se genera automáticamente mediante una estrategia de identidad.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Clave lógica de la traducción.
     *
     * <p>Ejemplo: {@code error.text.required}, {@code button.analyze}.</p>
     */
    @Column(name = "translation_key", nullable = false)
    private String key;

    /**
     * Código del idioma de la traducción.
     *
     * <p>Corresponde al estándar ISO (por ejemplo: {@code es}, {@code en}, {@code pt}).</p>
     */
    @Column(name = "language_code", nullable = false, length = 5)
    private String languageCode;

    /**
     * Texto traducido correspondiente a la clave y al idioma.
     *
     * <p>Se almacena como {@code TEXT} para permitir traducciones de longitud variable.</p>
     */
    @Column(name = "translated_text", columnDefinition = "TEXT")
    private String translatedText;

    /**
     * Módulo o contexto al que pertenece la traducción.
     *
     * <p>Permite agrupar traducciones por funcionalidad o dominio
     * (por ejemplo: auth, sentiment, ui).</p>
     */
    private String module;

    /**
     * Hash del texto fuente original.
     *
     * <p>Se utiliza para detectar cambios en el texto original y determinar
     * si una traducción debe ser actualizada.</p>
     */
    @Column(name = "source_hash")
    private String sourceHash;

    /**
     * Fecha y hora de creación del registro de traducción.
     *
     * <p>Este campo se asigna automáticamente al persistir la entidad
     * y no puede ser modificado posteriormente.</p>
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de la traducción.
     *
     * <p>Se actualiza automáticamente cada vez que la entidad es modificada.</p>
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relación con la entidad {@link Language}.
     *
     * <p>Se basa en el código de idioma ({@code language_code}) y es de solo lectura,
     * ya que el valor se gestiona directamente mediante el campo {@code languageCode}.</p>
     */
    @ManyToOne
    @JoinColumn(
            name = "language_code",
            referencedColumnName = "code",
            insertable = false,
            updatable = false
    )
    private Language language;

    /**
     * Callback ejecutado antes de persistir la entidad.
     *
     * <p>Inicializa automáticamente los campos {@code createdAt} y {@code updatedAt}
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
     * <p>Actualiza el campo {@code updatedAt} con la fecha y hora actuales.</p>
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}