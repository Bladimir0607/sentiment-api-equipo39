package com.hackaton.sentiment.service;

import com.hackaton.sentiment.entity.User;
import com.hackaton.sentiment.entity.UserRole;
import com.hackaton.sentiment.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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