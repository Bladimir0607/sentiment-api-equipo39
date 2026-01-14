-- =============================================================================
-- MIGRACIÓN: Sistema de Multi-idioma y Traducciones
-- DESCRIPCIÓN: Crea las tablas maestras para internacionalización (i18n).
-- =============================================================================

-- 1. Crear tabla de idiomas (Tabla Maestra)
-- Se usa IF NOT EXISTS por seguridad en entornos de desarrollo rápido (Hackatón)
CREATE TABLE IF NOT EXISTS languages (
    code VARCHAR(5) PRIMARY KEY, -- ISO Code (es, en, pt)
    name VARCHAR(100) NOT NULL,
    native_name VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Crear tabla de traducciones (Tabla de Relación)
CREATE TABLE IF NOT EXISTS translations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    translation_key VARCHAR(255) NOT NULL, -- Clave para buscar en el Front/Back
    language_code VARCHAR(5) NOT NULL,
    translated_text TEXT,
    module VARCHAR(50),      -- Para agrupar (ej. 'auth', 'sentiment', 'errors')
    source_hash VARCHAR(64), -- Para control de versiones del texto original
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Restricciones y Optimización
    UNIQUE KEY uk_translation_key_lang (translation_key, language_code),
    INDEX idx_language_code (language_code),
    INDEX idx_translation_key (translation_key),
    CONSTRAINT fk_translation_language FOREIGN KEY (language_code)
    REFERENCES languages(code) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Carga de Idiomas Iniciales
-- Se utiliza ON DUPLICATE KEY para permitir re-ejecuciones sin errores
INSERT INTO languages (code, name, native_name, is_default) VALUES
    ('es', 'Spanish', 'Español', TRUE),
    ('en', 'English', 'English', FALSE),
    ('pt', 'Portuguese', 'Português', FALSE),
    ('fr', 'French', 'Français', FALSE),
    ('de', 'German', 'Deutsch', FALSE)
    ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    native_name = VALUES(native_name),
    is_default = VALUES(is_default);

-- 4. Carga de Traducciones de la Aplicación
-- Centralizamos los textos aquí para evitar "Hardcoding" en el código Java
INSERT INTO translations (translation_key, language_code, translated_text, module) VALUES
    ('sentiment.analyze.success', 'es', 'Análisis de sentimiento completado', 'sentiment'),
    ('sentiment.analyze.success', 'en', 'Sentiment analysis completed', 'sentiment'),
    ('sentiment.analyze.success', 'pt', 'Análise de sentimento concluída', 'sentiment'),
    ('error.text.required', 'es', 'El texto no puede estar vacío', 'errors'),
    ('error.text.required', 'en', 'Text cannot be empty', 'errors'),
    ('error.text.required', 'pt', 'O texto não pode estar vazio', 'errors'),
    ('sentiment.label.positive', 'es', 'Positivo', 'labels'),
    ('sentiment.label.positive', 'en', 'Positive', 'labels'),
    ('sentiment.label.positive', 'pt', 'Positivo', 'labels'),
    ('sentiment.label.negative', 'es', 'Negativo', 'labels'),
    ('sentiment.label.negative', 'en', 'Negative', 'labels'),
    ('sentiment.label.negative', 'pt', 'Negativo', 'labels'),
    ('sentiment.label.neutral', 'es', 'Neutro', 'labels'),
    ('sentiment.label.neutral', 'en', 'Neutral', 'labels'),
    ('sentiment.label.neutral', 'pt', 'Neutro', 'labels')
    ON DUPLICATE KEY UPDATE
    translated_text = VALUES(translated_text),
    module = VALUES(module);