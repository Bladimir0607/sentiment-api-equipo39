package com.hackaton.sentiment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sentiment_analysis")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String text;

    // ✅ NUEVOS CAMPOS AGREGADOS
    @Column(name = "original_text", length = 500)
    private String originalText; // Texto original en el idioma del usuario

    @Column(name = "original_language", length = 10)
    private String originalLanguage; // 'es', 'en', 'pt'

    @Column(nullable = false, length = 50)
    private String label;

    @Column(nullable = false)
    private Double probability;

    @Column(length = 1000)
    private String keywords; // Palabras clave separadas por comas

    // Relación con el usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
