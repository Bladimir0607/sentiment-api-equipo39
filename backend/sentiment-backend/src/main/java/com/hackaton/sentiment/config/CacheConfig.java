package com.hackaton.sentiment.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Configuración de caché basada en Redis para el sistema.
 *
 * <p>Esta clase define la configuración global del mecanismo de caching
 * utilizado por la aplicación, con el objetivo de mejorar el rendimiento
 * y reducir llamadas repetitivas a servicios externos como LibreTranslate.</p>
 *
 * <p>Se habilita el soporte de caché mediante {@link EnableCaching} y se
 * configura un {@link RedisCacheManager} personalizado.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 *
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Crea y configura el {@link RedisCacheManager} utilizado por la aplicación.
     *
     * <p>Las principales características de esta configuración son:</p>
     * <ul>
     *   <li>Tiempo de vida (TTL) de los elementos en caché de 1 hora</li>
     *   <li>Deshabilitación del almacenamiento de valores nulos</li>
     *   <li>Serialización de claves como {@link String}</li>
     *   <li>Serialización de valores en formato JSON</li>
     * </ul>
     *
     * <p>Este mecanismo es especialmente útil para cachear traducciones
     * y evitar llamadas repetidas al servicio de traducción.</p>
     *
     * @param connectionFactory fábrica de conexiones a Redis
     * @return instancia configurada de {@link RedisCacheManager}
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // TTL de 1 hora
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(RedisSerializer.json())
                );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}