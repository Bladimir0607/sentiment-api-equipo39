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
 * Configuración principal de seguridad de la aplicación.
 *
 * <p>Esta clase define la configuración de Spring Security utilizando
 * autenticación basada en JWT (JSON Web Tokens). Se encarga de establecer
 * las reglas de autorización, la política de sesiones y la integración
 * del filtro de autenticación JWT.</p>
 *
 * <p>Características principales:</p>
 * <ul>
 *   <li>Arquitectura stateless para APIs REST</li>
 *   <li>Autenticación y autorización basada en JWT</li>
 *   <li>Protección de endpoints por roles</li>
 *   <li>Integración con {@link UserDetailsService}</li>
 * </ul>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Configura la cadena de filtros de seguridad de Spring Security.
     *
     * <p>Incluye:</p>
     * <ul>
     *   <li>Deshabilitación de CSRF para APIs REST</li>
     *   <li>Configuración de CORS</li>
     *   <li>Definición de endpoints públicos y protegidos</li>
     *   <li>Política de sesión stateless</li>
     *   <li>Registro del filtro de autenticación JWT</li>
     * </ul>
     *
     * @param http configurador de seguridad HTTP
     * @return {@link SecurityFilterChain} configurado
     * @throws Exception si ocurre un error durante la configuración
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
     * Crea el filtro de autenticación JWT.
     *
     * <p>Este filtro intercepta las peticiones HTTP, valida el token JWT
     * presente en la cabecera Authorization y establece el contexto de
     * seguridad si el token es válido.</p>
     *
     * @return instancia de {@link JwtAuthenticationFilter}
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    /**
     * Configura el proveedor de autenticación de Spring Security.
     *
     * <p>Utiliza {@link DaoAuthenticationProvider} para autenticar usuarios
     * a partir del {@link UserDetailsService} y un codificador de contraseñas
     * basado en BCrypt.</p>
     *
     * @return {@link AuthenticationProvider} configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Expone el {@link AuthenticationManager} para su uso en el proceso
     * de autenticación.
     *
     * @param config configuración de autenticación de Spring
     * @return {@link AuthenticationManager}
     * @throws Exception si ocurre un error al obtener el administrador
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define el codificador de contraseñas de la aplicación.
     *
     * <p>Se utiliza {@link BCryptPasswordEncoder}, un algoritmo seguro
     * recomendado para el hashing de contraseñas.</p>
     *
     * @return instancia de {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
