package com.hackaton.sentiment.config;

import com.hackaton.sentiment.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.jspecify.annotations.NonNull;

import java.io.IOException;

/**
 * Filtro de autenticación JWT que procesa tokens Bearer en las solicitudes HTTP.
 *
 * Este filtro intercepta cada solicitud entrante, extrae y valida tokens JWT del encabezado
 * Authorization, y establece la autenticación en el contexto de seguridad de Spring Security
 * cuando el token es válido.
 *
 * Extiende {@link OncePerRequestFilter} para garantizar una única ejecución por solicitud
 * y se integra con el ecosistema de seguridad de Spring.
 */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Procesa cada solicitud HTTP para autenticación JWT.
     *
     * El método realiza los siguientes pasos:
     *
     *  1. Extrae el encabezado Authorization de la solicitud
     *  2. Verifica si contiene un token Bearer
     *  3. Extrae y valida el token JWT
     *  4. Carga los detalles del usuario si el token es válido
     *  5. Establece la autenticación en el contexto de seguridad<
     *
     * Si no hay token o es inválido, la solicitud continúa sin autenticación.</p>
     *
     * @param request La solicitud HTTP entrante
     * @param response La respuesta HTTP
     * @param filterChain Cadena de filtros para continuar el procesamiento
     * @throws ServletException Si ocurre un error en el procesamiento del servlet
     * @throws IOException Si ocurre un error de E/S
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

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}