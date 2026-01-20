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
 *
 * Proporciona métodos para la creación de usuarios y la inicialización de
 * datos por defecto en el sistema.
 *
 * Incluye funcionalidades básicas de gestiónde usuarios como verificación de existencia
 * y creación con encriptación de contraseñas.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crea un nuevo usuario en el sistema con el rol de usuario estándar (USER).
     * Valida que el nombre de usuario no exista previamente, encripta la contraseña
     * y genera automáticamente un email basado en el nombre de usuario.
     *
     * @param username Nombre de usuario único para el nuevo usuario
     * @param password Contraseña en texto plano que será encriptada
     * @return El usuario creado y persistido en la base de datos
     * @throws RuntimeException Si el nombre de usuario ya existe en el sistema
     */
    public User createUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("El usuario ya existe");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(username + "@hackaton.com")
                .role(UserRole.USER) //Enum no String
                .build();

        return userRepository.save(user);
    }

    /**
     * Inicializa un usuario administrador por defecto cuando la aplicación se inicia.
     * Este método se ejecuta automáticamente tras la construcción del bean y crea
     * un usuario con credenciales predefinidas si no existe, facilitando el acceso
     * inicial al sistema durante el desarrollo y pruebas.
     */
    @PostConstruct
    public void loadDefaultUser() {
        if (!userRepository.existsByUsername("hackaton")) {
            User defaultUser = User.builder()
                    .username("hackaton")
                    .password(passwordEncoder.encode("hackaton123"))
                    .email("hackaton@noucountry.com")
                    .role(UserRole.ADMIN) // Enum no String
                    .build();
            userRepository.save(defaultUser);
            System.out.println("Usuario por defecto creado: hackaton / hackaton123");
        }
    }
}