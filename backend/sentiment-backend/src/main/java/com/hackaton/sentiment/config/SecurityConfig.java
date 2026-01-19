package com.hackaton.sentiment.config;

import com.hackaton.sentiment.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración principal de seguridad para la aplicación.
 *
 * Esta clase configura Spring Security para usar autenticación basada en JWT,
 * establece políticas de autorización para diferentes endpoints, y define la cadena
 * de filtros de seguridad.
 *
 * La configuración incluye:
 *   1. Deshabilitación de CSRF para API REST stateless
 *   2. Configuración de CORS
 *   3. Definición de reglas de autorización por endpoints
 *   4. Configuración de sesiones stateless
 *   5. Integración del filtro JWT en la cadena de seguridad
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Configura la cadena de filtros de seguridad principal.
     *
     * Define la configuración de seguridad HTTP incluyendo:
     *   1. Endpoints públicos (autenticación, health check, documentación).
     *   2. Endpoints restringidos por roles
     *   3. Política de sesiones stateless
     *   4. Integración del filtro JWT de autenticación
     *
     * @param http Configurador de seguridad HTTP de Spring
     * @return La cadena de filtros de seguridad configurada
     * @throws Exception Si ocurre un error en la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/health",
                                "/api/i18n/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    /**
     * Crea y configura el filtro de autenticación JWT.
     *
     * @return Instancia configurada de {@link JwtAuthenticationFilter}
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    /**
     * Configura el proveedor de autenticación para Spring Security.
     *
     * Utiliza {@link DaoAuthenticationProvider} que integra con el {@link UserDetailsService}
     * proporcionado y el codificador de contraseñas configurado.
     *
     * @return Proveedor de autenticación configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Expone el {@link AuthenticationManager} para uso en la aplicación.
     *
     * @param config Configuración de autenticación proporcionada por Spring
     * @return AuthenticationManager configurado
     * @throws Exception Si ocurre un error al obtener el AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configura el codificador de contraseñas para la aplicación.
     *
     * Utiliza {@link BCryptPasswordEncoder} que es un codificador seguro
     * basado en el algoritmo BCrypt para hashing de contraseñas.
     *
     * @return Instancia de PasswordEncoder usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
