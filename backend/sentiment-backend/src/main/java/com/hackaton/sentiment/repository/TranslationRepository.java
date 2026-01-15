package com.hackaton.sentiment.repository;

import com.hackaton.sentiment.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar traducciones dinámicas.
 *
 * MÉTODOS DISPONIBLES:
 * - findByLanguageCode: Obtiene todas las traducciones de un idioma
 * - findByKeyAndLanguageCode: Busca traducción específica
 * - findByModuleAndLanguageCode: Agrupa por módulo (frontend, backend, etc.)
 * - existsByKeyAndLanguageCode: Verifica si existe traducción
 *
 * USADO POR: TranslationService.java
 */
@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {

    /**
     * Encuentra todas las traducciones para un idioma específico.
     * Usado por: TranslationService.getAllTranslationsForLanguage()
     *
     * @param languageCode Código de idioma (es, en, pt)
     * @return Lista de traducciones
     */
    List<Translation> findByLanguageCode(String languageCode);

    /**
     * Busca una traducción específica por clave e idioma.
     * Usado por: TranslationService.tryDatabase()
     *
     * @param key Clave de traducción (ej: "error.text.required")
     * @param languageCode Código de idioma (es, en, pt)
     * @return Optional con la traducción si existe
     */
    Optional<Translation> findByKeyAndLanguageCode(String key, String languageCode);

    /**
     * Encuentra traducciones por módulo e idioma.
     * Útil para cargar traducciones del frontend por separado.
     *
     * @param module Módulo (frontend, backend, errors, etc.)
     * @param languageCode Código de idioma
     * @return Lista de traducciones del módulo
     */
    List<Translation> findByModuleAndLanguageCode(String module, String languageCode);

    /**
     * Verifica si existe una traducción sin cargarla.
     * Usado por: TranslationService.saveTranslationToDatabase()
     *
     * @param key Clave de traducción
     * @param languageCode Código de idioma
     * @return true si existe, false si no
     */
    boolean existsByKeyAndLanguageCode(String key, String languageCode);

    /**
     * MÉTODO ADICIONAL (OPCIONAL): Buscar traducciones por patrón de clave.
     * Útil para búsquedas como "error.*" o "button.*"
     *
     * @param keyPattern Patrón de búsqueda (ej: "button%")
     * @param languageCode Código de idioma
     * @return Lista de traducciones que coinciden
     */
    List<Translation> findByKeyStartingWithAndLanguageCode(String keyPattern, String languageCode);
}