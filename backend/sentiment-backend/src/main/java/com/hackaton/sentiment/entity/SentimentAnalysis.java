package com.hackaton.sentiment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un análisis de sentimiento realizado sobre un texto.
 *
 * <p>Esta entidad almacena el resultado de un análisis de sentimiento generado
 * por el sistema, incluyendo el texto original, la etiqueta de sentimiento
 * (por ejemplo, positivo o negativo), la probabilidad asociada y el usuario
 * que realizó la solicitud.</p>
 *
 * <p>La información se persiste en la tabla {@code sentiment_analysis}.</p>
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Entity
@Table(name = "sentiment_analysis")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentAnalysis {

    /**
     * Identificador único del análisis de sentimiento.
     *
     * <p>Se genera automáticamente mediante una estrategia de identidad.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Texto original analizado por el sistema.
     *
     * <p>El texto tiene un tamaño máximo de 500 caracteres y no puede ser nulo.</p>
     */
    @Column(nullable = false, length = 500)
    private String text;

    /**
     * Etiqueta de sentimiento asignada al texto.
     *
     * <p>Ejemplos comunes: {@code POSITIVE}, {@code NEGATIVE}.</p>
     */
    @Column(nullable = false, length = 50)
    private String label;

    /**
     * Probabilidad asociada a la predicción del sentimiento.
     *
     * <p>Representa el nivel de confianza del modelo de machine learning
     * en la predicción realizada.</p>
     */
    @Column(nullable = false)
    private Double probability;

    /**
     * Usuario que realizó el análisis de sentimiento.
     *
     * <p>Relación muchos-a-uno con la entidad {@link User}. La carga es perezosa
     * ({@code LAZY}) para optimizar el rendimiento.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Fecha y hora en la que se creó el análisis.
     *
     * <p>Este campo se asigna automáticamente al persistir la entidad
     * y no puede ser modificado posteriormente.</p>
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Callback ejecutado antes de persistir la entidad.
     *
     * <p>Inicializa el campo {@code createdAt} con la fecha y hora actuales.</p>
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}