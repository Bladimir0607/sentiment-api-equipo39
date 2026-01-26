package com.hackaton.sentiment.repository;

import com.hackaton.sentiment.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de entidades {@link Translation} (traducciones dinámicas).
 * Extiende {@link JpaRepository} proporcionando operaciones CRUD básicas y métodos de consulta personalizados.
 *
 * <p>Este repositorio se utiliza principalmente por TranslationService
 * para operaciones relacionadas con la carga, búsqueda y verificación de traducciones en diferentes idiomas.</p>
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {

    /**
     * Recupera todas las traducciones disponibles para un código de idioma específico.
     *
     * @param languageCode el código del idioma (ej: "es", "en", "pt") - no debe ser {@code null}
     * @return una lista de {@link Translation} para el idioma especificado, puede estar vacía si no hay traducciones
     */
    List<Translation> findByLanguageCode(String languageCode);

    /**
     * Busca una traducción específica utilizando su clave única y el código de idioma.
     *
     * @param key la clave de la traducción (ej: "error.text.required") - no debe ser {@code null}
     * @param languageCode el código del idioma (ej: "es", "en", "pt") - no debe ser {@code null}
     * @return un {@link Optional} que contiene la {@link Translation} si existe, o {@link Optional#empty()} si no
     */
    Optional<Translation> findByKeyAndLanguageCode(String key, String languageCode);

    /**
     * Recupera todas las traducciones pertenecientes a un módulo específico y código de idioma.
     * Útil para cargar traducciones agrupadas por módulo (frontend, backend, errores, etc.).
     *
     * @param module el módulo al que pertenecen las traducciones (ej: "frontend", "backend", "errors") - no debe ser {@code null}
     * @param languageCode el código del idioma (ej: "es", "en", "pt") - no debe ser {@code null}
     * @return una lista de {@link Translation} del módulo especificado, puede estar vacía si no hay traducciones
     */
    List<Translation> findByModuleAndLanguageCode(String module, String languageCode);

    /**
     * Verifica si existe una traducción para una clave y código de idioma específicos.
     * Este método es más eficiente que cargar la entidad completa cuando solo se necesita verificar existencia.
     *
     * @param key la clave de la traducción (ej: "error.text.required") - no debe ser {@code null}
     * @param languageCode el código del idioma (ej: "es", "en", "pt") - no debe ser {@code null}
     * @return {@code true} si existe una traducción con la clave e idioma especificados, {@code false} en caso contrario
     */
    boolean existsByKeyAndLanguageCode(String key, String languageCode);

    /**
     * Busca traducciones cuyas claves comiencen con un patrón específico y que correspondan a un código de idioma.
     * Útil para búsquedas agrupadas como "error.*" o "button.*".
     *
     * @param keyPattern el patrón de búsqueda para las claves (ej: "button%", "error.%") - no debe ser {@code null}
     * @param languageCode el código del idioma (ej: "es", "en", "pt") - no debe ser {@code null}
     * @return una lista de {@link Translation} que coinciden con el patrón, puede estar vacía si no hay coincidencias
     */
    List<Translation> findByKeyStartingWithAndLanguageCode(String keyPattern, String languageCode);
}