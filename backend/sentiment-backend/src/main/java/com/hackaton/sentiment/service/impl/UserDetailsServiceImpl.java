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
 * Este servicio es responsable de cargar los detalles de un usuario desde la base de datos
 * utilizando su nombre de usuario, para ser utilizado por el sistema de autenticación
 * y autorización de Spring Security.
 *
 * La anotación {@link Primary} indica que esta implementación será la principal
 * cuando existan múltiples beans del tipo {@link UserDetailsService}.
 */
    @Service
    @Primary
    @RequiredArgsConstructor
    public class UserDetailsServiceImpl implements UserDetailsService {

        private final UserRepository userRepository;

    /**
     * Carga los detalles de un usuario por su nombre de usuario.
     *
     * Busca al usuario en la base de datos utilizando el repositorio de usuarios.
     * Si el usuario no es encontrado, lanza una excepción {@link UsernameNotFoundException}.
     *
     * @param username Nombre de usuario a buscar
     * @return {@link UserDetails} con la información del usuario para Spring Security
     * @throws UsernameNotFoundException si el usuario no existe en la base de datos
     */
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        }

    }