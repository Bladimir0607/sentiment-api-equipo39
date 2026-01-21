package com.hackaton.sentiment.service;

import com.hackaton.sentiment.entity.User;
import com.hackaton.sentiment.entity.UserRole;
import com.hackaton.sentiment.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio para la gestión de usuarios.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param username nombre de usuario
     * @param password contraseña
     * @return usuario creado
     * @throws RuntimeException si el usuario ya existe
     */
    public User createUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("El usuario ya existe");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(username + "@hackaton.com")
                .role(UserRole.USER)
                .build();

        return userRepository.save(user);
    }

    /**
     * Inicializa un usuario administrador por defecto.
     */
    @PostConstruct
    public void loadDefaultUser() {
        if (!userRepository.existsByUsername("hackaton")) {
            User defaultUser = User.builder()
                    .username("hackaton")
                    .password(passwordEncoder.encode("hackaton123"))
                    .email("hackaton@noucountry.com")
                    .role(UserRole.ADMIN)
                    .build();
            userRepository.save(defaultUser);
            System.out.println("Usuario por defecto creado: hackaton / hackaton123");
        }
    }
}