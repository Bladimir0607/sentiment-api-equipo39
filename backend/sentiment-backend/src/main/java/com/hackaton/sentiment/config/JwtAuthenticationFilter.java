package com.hackaton.sentiment.config;

import com.hackaton.sentiment.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación basado en JWT (JSON Web Token).
 *
 * <p>Este filtro intercepta todas las solicitudes HTTP entrantes y verifica
 * la presencia de un token JWT en el encabezado {@code Authorization}
 * utilizando el esquema {@code Bearer}.</p>
 *
 * <p>Cuando el token es válido, se establece la autenticación correspondiente
 * en el {@link SecurityContextHolder} de Spring Security, permitiendo el
 * acceso a recursos protegidos.</p>
 *
 * <p>Extiende {@link OncePerRequestFilter} para garantizar que el filtro
 * se ejecute una sola vez por cada solicitud.</p>
 *
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 *
 * @see JwtService
 * @see UserDetailsService
 * @see OncePerRequestFilter
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Servicio encargado de operaciones relacionadas con JWT.
     *
     * <p>Responsable de extraer el usuario del token y validar su integridad,
     * firma y expiración.</p>
     */
    private final JwtService jwtService;

    /**
     * Servicio para la carga de detalles del usuario.
     *
     * <p>Se utiliza para obtener un {@link UserDetails} a partir del
     * identificador extraído del token JWT.</p>
     */
    private final UserDetailsService userDetailsService;

    /**
     * Procesa cada solicitud HTTP para autenticación basada en JWT.
     *
     * <p>Flujo de ejecución:</p>
     * <ol>
     *   <li>Obtiene el encabezado {@code Authorization}</li>
     *   <li>Verifica que contenga un token con prefijo {@code Bearer }</li>
     *   <li>Extrae el JWT del encabezado</li>
     *   <li>Obtiene el identificador del usuario desde el token</li>
     *   <li>Válida el token contra los datos del usuario</li>
     *   <li>Establece la autenticación en el contexto de seguridad</li>
     * </ol>
     *
     * <p>Si el token no existe, es inválido o está expirado, la solicitud
     * continúa sin autenticación.</p>
     *
     * @param request      solicitud HTTP entrante
     * @param response     respuesta HTTP
     * @param filterChain  cadena de filtros de Spring Security
     * @throws ServletException si ocurre un error en el procesamiento del servlet
     * @throws IOException      sí ocurre un error de entrada/salida
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Si no hay encabezado Authorization o no es Bearer, continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token JWT
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        // Validar y autenticar solo si no existe autenticación previa
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails =
                    this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
