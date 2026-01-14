package com.hackaton.sentiment.repository;

import com.hackaton.sentiment.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {

    List<Translation> findByLanguageCode(String languageCode);
    Optional<Translation> findByKeyAndLanguageCode(String key, String languageCode);
    List<Translation> findByModuleAndLanguageCode(String module, String languageCode);
    boolean existsByKeyAndLanguageCode(String key, String languageCode);

    @Query("SELECT t.key, t.translatedText FROM Translation t WHERE t.languageCode = :languageCode")
    List<Object[]> findKeyValuePairs(String languageCode);
}