package com.hackaton.sentiment.repository;

import com.hackaton.sentiment.entity.SentimentAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentimentAnalysisRepository extends JpaRepository<SentimentAnalysis, Long> {

    long countByLabel(String label);
}