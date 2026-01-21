-- ============================================================
-- SCRIPT DE INICIALIZACIÓN PARA LA BASE DE DATOS SENTIMENTDB
-- ============================================================

/**
 * Crea la base de datos sentimentdb si no existe.
 */
CREATE DATABASE IF NOT EXISTS sentimentdb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sentimentdb;

-- ============================================================
-- TABLA: sentiment_analysis (ya existente en tu proyecto)
-- ============================================================

/**
 * Tabla para almacenar análisis de sentimiento.
 */
CREATE TABLE IF NOT EXISTS sentiment_analysis (
                                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                  text VARCHAR(500) NOT NULL,
    label VARCHAR(50) NOT NULL,
    probability DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- PREPARACIÓN PARA CLAVES FORÁNEAS
-- ============================================================

/**
 * Desactiva temporalmente las restricciones de claves foráneas.
 */
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- TABLA: languages (idiomas soportados)
-- ============================================================

/**
 * Tabla de idiomas soportados por la aplicación.
 */
CREATE TABLE IF NOT EXISTS languages (
                                         code VARCHAR(5) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    native_name VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLA: translations (traducciones)
-- ============================================================

/**
 * Tabla de traducciones de texto para múltiples idiomas.
 */
CREATE TABLE IF NOT EXISTS translations (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            translation_key VARCHAR(255) NOT NULL,
    language_code VARCHAR(5) NOT NULL,
    translated_text TEXT,
    module VARCHAR(50),
    source_hash VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_translation_key_lang (translation_key, language_code),
    KEY idx_language_code (language_code),
    KEY idx_translation_key (translation_key),

    CONSTRAINT fk_translation_language FOREIGN KEY (language_code)
    REFERENCES languages(code) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- RESTAURAR RESTRICCIONES
-- ============================================================

/**
 * Reactiva las restricciones de claves foráneas.
 */
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- INSERTAR IDIOMAS INICIALES
-- ============================================================

/**
 * Inserta los idiomas iniciales soportados por la aplicación.
 */
INSERT IGNORE INTO languages (code, name, native_name, active, is_default) VALUES
                                                                               ('es', 'Spanish', 'Español', TRUE, TRUE),     -- Español (idioma por defecto)
                                                                               ('en', 'English', 'English', TRUE, FALSE),    -- Inglés
                                                                               ('pt', 'Portuguese', 'Português', TRUE, FALSE), -- Portugués (Brazil)
                                                                               ('fr', 'French', 'Français', TRUE, FALSE),    -- Francés
                                                                               ('de', 'German', 'Deutsch', TRUE, FALSE);     -- Alemán

-- ============================================================
-- INSERTAR TRADUCCIONES BÁSICAS
-- ============================================================

/**
 * Inserta las traducciones básicas para los idiomas soportados.
 */
INSERT IGNORE INTO translations (translation_key, language_code, translated_text) VALUES
                                                                                      -- Español
                                                                                      ('sentiment.analyze.success', 'es', 'Análisis de sentimiento completado'),
                                                                                      ('error.text.required', 'es', 'El texto no puede estar vacío'),
                                                                                      ('error.text.length', 'es', 'El texto debe tener entre 5 y 500 caracteres'),
                                                                                      ('sentiment.label.positive', 'es', 'Positivo'),
                                                                                      ('sentiment.label.negative', 'es', 'Negativo'),
                                                                                      ('sentiment.label.neutral', 'es', 'Neutro'),
                                                                                      ('error.internal.server', 'es', 'Error interno del servidor'),
                                                                                      ('sentiment.stats.total', 'es', 'Total análisis'),
                                                                                      ('sentiment.stats.positive', 'es', 'Positivos'),
                                                                                      ('sentiment.stats.negative', 'es', 'Negativos'),
                                                                                      ('sentiment.stats.neutral', 'es', 'Neutros'),

                                                                                      -- Inglés
                                                                                      ('sentiment.analyze.success', 'en', 'Sentiment analysis completed'),
                                                                                      ('error.text.required', 'en', 'Text cannot be empty'),
                                                                                      ('error.text.length', 'en', 'Text must be between 5 and 500 characters'),
                                                                                      ('sentiment.label.positive', 'en', 'Positive'),
                                                                                      ('sentiment.label.negative', 'en', 'Negative'),
                                                                                      ('sentiment.label.neutral', 'en', 'Neutral'),
                                                                                      ('error.internal.server', 'en', 'Internal server error'),
                                                                                      ('sentiment.stats.total', 'en', 'Total analyses'),
                                                                                      ('sentiment.stats.positive', 'en', 'Positive'),
                                                                                      ('sentiment.stats.negative', 'en', 'Negative'),
                                                                                      ('sentiment.stats.neutral', 'en', 'Neutral'),

                                                                                      -- Portugués
                                                                                      ('sentiment.analyze.success', 'pt', 'Análise de sentimento concluída'),
                                                                                      ('error.text.required', 'pt', 'O texto não pode estar vazio'),
                                                                                      ('error.text.length', 'pt', 'O texto deve ter entre 5 e 500 caracteres'),
                                                                                      ('sentiment.label.positive', 'pt', 'Positivo'),
                                                                                      ('sentiment.label.negative', 'pt', 'Negativo'),
                                                                                      ('sentiment.label.neutral', 'pt', 'Neutro'),
                                                                                      ('error.internal.server', 'pt', 'Erro interno do servidor'),
                                                                                      ('sentiment.stats.total', 'pt', 'Total de análises'),
                                                                                      ('sentiment.stats.positive', 'pt', 'Positivos'),
                                                                                      ('sentiment.stats.negative', 'pt', 'Negativos'),
                                                                                      ('sentiment.stats.neutral', 'pt', 'Neutros');

-- ============================================================
-- MENSAJE DE ÉXITO
-- ============================================================

/**
 * Mensajes de confirmación de la inicialización exitosa.
 */
SELECT '✅ Base de datos inicializada correctamente' AS message;
SELECT '📊 Tablas creadas: sentiment_analysis, languages, translations' AS detalle;
SELECT CONCAT('🌍 Idiomas insertados: ', COUNT(*)) AS resultado FROM languages;
SELECT CONCAT('📝 Traducciones insertadas: ', COUNT(*)) AS resultado FROM translations;