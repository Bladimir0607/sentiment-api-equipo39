package com.hackaton.sentiment.repository;

import com.hackaton.sentiment.entity.User;
import com.hackaton.sentiment.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad User.
 *  @author Equipo Hackathon Oracle ONE - Backend
 *  @version 1.4
 *  @since 2026-01-21
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario
     * @return usuario encontrado
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por su email.
     *
     * @param email dirección de email
     * @return usuario encontrado
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el nombre de usuario especificado.
     *
     * @param username nombre de usuario
     * @return true si existe, false si no
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email especificado.
     *
     * @param email dirección de email
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

    /**
     * Cuenta el número de usuarios con un rol específico.
     * Método para estadísticas de administrador.
     *
     * @param role rol del usuario
     * @return número de usuarios con ese rol
     */
    long countByRole(UserRole role);
}