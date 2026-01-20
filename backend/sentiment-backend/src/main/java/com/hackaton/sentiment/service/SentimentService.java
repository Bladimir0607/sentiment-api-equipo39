package com.hackaton.sentiment.service;

import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.dto.response.SentimentStatsResponseDTO;
import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.entity.User;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Interfaz de servicio para operaciones relacionadas con análisis de sentimiento.
 *
 * Define los métodos disponibles para procesar análisis de texto, obtener
 * estadísticas y gestionar el historial de análisis de los usuarios. Las implementaciones
 * deben proporcionar la lógica de negocio para estas operaciones.
 *
 * Los métodos están organizados según los permisos requeridos:
 *
 *   1. Métodos disponibles para todos los usuarios autenticados.
 *   2. Métodos exclusivos para usuarios con rol de administrador.
 */
public interface SentimentService {

    /**
     * Analiza el sentimiento de un texto proporcionado.
     *
     * Procesa el texto a través del servicio de machine learning, normaliza la
     * respuesta y guarda el análisis asociado al usuario autenticado.

     * @param request DTO que contiene el texto a analizar
     * @return DTO con la respuesta del análisis incluyendo etiqueta y probabilidad
     */
    @Transactional
    SentimentResponseDTO analyzeSentiment(SentimentRequestDTO request);

    /**
     * Obtiene estadísticas globales de los análisis de sentimiento.
     *
     * Calcula métricas agregadas como total de análisis, análisis positivos
     * y análisis negativos registrados en el sistema.
     *
     * @return DTO con las estadísticas calculadas
     */
    SentimentStatsResponseDTO getStats();

    /**
     * Obtiene todos los análisis realizados por el usuario actualmente autenticado.
     *
     * Este método está disponible para cualquier usuario autenticado y retorna
     * únicamente su propio historial de análisis.
     *
     * @return Lista de análisis de sentimiento del usuario autenticado
     *
     * @see SentimentAnalysis
     */
    List<SentimentAnalysis> getMyAnalyses();

    /**
     * Obtiene todos los análisis de sentimiento del sistema.
     *
     * Este método está restringido a usuarios con permisos de administrador
     * y retorna el historial completo de análisis de todos los usuarios.
     *
     * ADMIN - ver todos los análisis
     *
     * @return Lista completa de todos los análisis de sentimiento
     *
     * @see SentimentAnalysis
     */
    List<SentimentAnalysis> getAllAnalyses();

    /**
     * Obtiene estadísticas avanzadas del sistema.
     *
     * Proporciona métricas detalladas para fines administrativos, incluyendo
     * información como promedio de análisis por usuario y distribución temporal.
     *
     * @return Objeto con las estadísticas avanzadas (puede ser un DTO o Map)
     */
    Object getAdvancedStats();

    /**
     * Elimina todos los análisis de sentimiento asociados a un usuario.
     *
     * Utilizado cuando se elimina una cuenta de usuario para mantener la
     * integridad referencial de la base de datos.
     *
     * @param user Usuario cuyos análisis se desean eliminar
     *
     * @see User
     */
    @Transactional
    void deleteAnalysesByUser(User user);

    /**
     * Obtiene los análisis de sentimiento de un usuario específico.
     *
     * <p>Este método está restringido a administradores y permite auditar
     * la actividad de análisis de cualquier usuario del sistema.</p>
     *
     * ADMIN - ver análisis de un usuario específico
     *
     * @param userId ID del usuario cuyos análisis se desean consultar
     * @return Lista de análisis del usuario especificado
     *
     * @see SentimentAnalysis
     */
    List<SentimentAnalysis> getUserAnalyses(Long userId);
}