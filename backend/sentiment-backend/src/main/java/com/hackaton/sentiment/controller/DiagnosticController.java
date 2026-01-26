package com.hackaton.sentiment.controller;

import com.hackaton.sentiment.client.LibreTranslateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/diagnostic")
@RequiredArgsConstructor
@Slf4j
public class DiagnosticController {

    private final LibreTranslateClient libreTranslateClient;

    @PostMapping("/test-translation")
    public Map<String, Object> testTranslation(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String sourceLang = request.get("source") != null ? request.get("source") : "en";
        String targetLang = request.get("target") != null ? request.get("target") : "es";

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", new java.util.Date());
        response.put("text", text);
        response.put("source", sourceLang);
        response.put("target", targetLang);

        try {
            // Test 1: Verificar conexión
            response.put("libreTranslateAvailable", libreTranslateClient.isServiceAvailable());

            // Test 2: Traducción simple
            String translated = libreTranslateClient.translate(text, sourceLang, targetLang);
            response.put("translated", translated);
            response.put("translationSuccess", true);

            // Test 3: Verificar caracteres españoles
            boolean hasSpanishChars = translated.matches(".*[áéíóúñÁÉÍÓÚÑ].*");
            response.put("hasSpanishChars", hasSpanishChars);

            // Test 4: Comparación
            response.put("sameAsOriginal", translated.equals(text));

        } catch (Exception e) {
            response.put("translationSuccess", false);
            response.put("error", e.getMessage());
            response.put("errorClass", e.getClass().getName());
        }

        return response;
    }

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", new java.util.Date());
        health.put("service", "sentiment-analysis");

        try {
            boolean libreTranslateOk = libreTranslateClient.isServiceAvailable();
            health.put("libreTranslate", libreTranslateOk ? "UP" : "DOWN");
        } catch (Exception e) {
            health.put("libreTranslate", "ERROR: " + e.getMessage());
        }

        return health;
    }
}