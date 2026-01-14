package com.hackaton.sentiment.dto.response;

import com.hackaton.sentiment.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Campo para mostrar de forma amigable
    public String getRoleDisplay() {
        if (role == null) return "No definido";
        return switch (role) {
            case ADMIN -> "Administrador";
            case USER -> "Usuario";
            default -> role.name();
        };
    }
}