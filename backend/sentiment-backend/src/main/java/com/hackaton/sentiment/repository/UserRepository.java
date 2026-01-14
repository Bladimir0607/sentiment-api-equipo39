package com.hackaton.sentiment.repository;

import com.hackaton.sentiment.entity.User;
import com.hackaton.sentiment.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    //metodo para estadisticas de Admin
    long countByRole(UserRole role);
}