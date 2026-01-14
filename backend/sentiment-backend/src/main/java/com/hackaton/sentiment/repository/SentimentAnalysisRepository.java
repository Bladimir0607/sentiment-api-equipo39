package com.hackaton.sentiment.repository;

import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysis, Long> {

    long countByLabel(String label);

    // Buscar análisis por usuario
    List<SentimentAnalysis> findByUser(User user);

    //Buscar análisis por usuario ID
    List<SentimentAnalysis> findByUserId(Long userId);

    // Solucion al problema
    @Query("SELECT sa FROM SentimentAnalysis sa JOIN FETCH sa.user u ORDER BY sa.createdAt DESC")
    List<SentimentAnalysis> findAllWithUser();
}