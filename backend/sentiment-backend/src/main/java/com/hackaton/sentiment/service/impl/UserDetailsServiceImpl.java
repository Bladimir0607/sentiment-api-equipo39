package com.hackaton.sentiment.service.impl;

import com.hackaton.sentiment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de detalles de usuario para Spring Security.
 *
 * <p>Este servicio carga los detalles de un usuario desde la base de datos
 * utilizando su nombre de usuario para la autenticación de Spring Security.</p>
 *
 * <p>La anotación {@link Primary} marca esta implementación como la principal
 * cuando existen múltiples beans de tipo {@link UserDetailsService}.</p>
 *
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Service
@Primary
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carga los detalles de un usuario por su nombre de usuario.
     *
     * @param username el nombre de usuario a buscar
     * @return los detalles del usuario para Spring Security
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}