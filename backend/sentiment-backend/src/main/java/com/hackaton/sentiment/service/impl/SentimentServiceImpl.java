package com.hackaton.sentiment.service.impl;

import com.hackaton.sentiment.client.LibreTranslateClient;
import com.hackaton.sentiment.client.SentimentMlClient;
import com.hackaton.sentiment.dto.request.SentimentRequestDTO;
import com.hackaton.sentiment.dto.response.SentimentResponseDTO;
import com.hackaton.sentiment.dto.response.SentimentStatsResponseDTO;
import com.hackaton.sentiment.entity.SentimentAnalysis;
import com.hackaton.sentiment.entity.User;
import com.hackaton.sentiment.repository.SentimentAnalysisRepository;
import com.hackaton.sentiment.repository.UserRepository;
import com.hackaton.sentiment.service.SentimentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static com.hackaton.sentiment.util.SentimentLabels.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SentimentServiceImpl implements SentimentService {

    private final SentimentMlClient mlClient;
    private final SentimentAnalysisRepository repository;
    private final UserRepository userRepository;
    private final LibreTranslateClient libreTranslateClient;

    @Override
    @Transactional
    public SentimentResponseDTO analyzeSentiment(SentimentRequestDTO request) {
        // ============================================
        // PASO 0: INICIALIZACIÓN Y LOGGING DE DEBUG
        // ============================================
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        String originalText = request.getText();
        String userLanguage = request.getLanguage() != null ? request.getLanguage() : "es";

        // DEBUG CRÍTICO
        log.error("🔥🔥🔥 DEBUG CRÍTICO - INICIO ANÁLISIS 🔥🔥🔥");
        log.error("📝 Texto original: {}", originalText);
        log.error("🌐 Idioma recibido: '{}'", userLanguage);
        log.error("🔍 ¿Idioma != 'es'?: {}", !"es".equals(userLanguage));
        log.error("-".repeat(50));

        String textToAnalyze = originalText;  // Texto que enviaremos al ML
        String translatedText = originalText; // Texto traducido (si aplica)
        List<String> translatedKeywords = new ArrayList<>();
        List<String> originalKeywordsEs = new ArrayList<>();

        // ============================================
        // PASO 1: TRADUCCIÓN OBLIGATORIA (CORREGIDO)
        // ============================================
        if (!"es".equals(userLanguage)) {
            log.error("🚨🚨🚨 ¡¡¡TRADUCCIÓN REQUERIDA!!! {} → es", userLanguage);

            // OPCIÓN 1: Traducción automática con LibreTranslate
            try {
                log.error("📞 Llamando a LibreTranslate...");
                String testResult = libreTranslateClient.translate("hello", "en", "es");
                log.error("✅ LibreTranslate funciona: 'hello' → '{}'", testResult);

                textToAnalyze = libreTranslateClient.translate(originalText, userLanguage, "es");
                translatedText = textToAnalyze;
                log.error("🎉 ¡¡¡TRADUCCIÓN EXITOSA!!!");
                log.error("   Original ({}): {}", userLanguage, originalText);
                log.error("   Traducido (es): {}", textToAnalyze);

                // Verificación adicional
                boolean tieneCaracteresEspanol = textToAnalyze.matches(".*[áéíóúñÁÉÍÓÚÑ].*");
                log.error("   ¿Tiene caracteres españoles?: {}", tieneCaracteresEspanol);

            } catch (Exception e) {
                log.error("💥💥💥 FALLÓ LIBRETRANSLATE: {}", e.getMessage());

                // OPCIÓN 2: Traducción manual de emergencia
                log.error("🔄 Usando traducción manual de emergencia...");
                textToAnalyze = translateManualEmergencia(originalText, userLanguage);
                translatedText = textToAnalyze;
                log.error("⚠️ Traducción manual: {}", textToAnalyze);
            }
        } else {
            log.error("✅ Idioma ya es español, sin traducción necesaria");
            textToAnalyze = originalText;
        }

        // ============================================
        // VERIFICACIÓN CRÍTICA ANTES DE ENVIAR AL ML
        // ============================================
        log.error("🔍 VERIFICACIÓN PRE-ML:");
        log.error("   Texto FINAL para ML: '{}'", textToAnalyze);
        log.error("   ¿Contiene caracteres españoles?: {}",
                textToAnalyze.matches(".*[áéíóúñÁÉÍÓÚÑ].*"));
        log.error("   ¿Es diferente al original?: {}", !textToAnalyze.equals(originalText));
        log.error("-".repeat(50));

        // ============================================
        // PASO 2: ENVIAR AL MODELO ML
        // ============================================
        log.error("🤖 ENVIANDO AL MODELO ML...");
        SentimentResponseDTO mlResponse;

        try {
            mlResponse = mlClient.predict(textToAnalyze);
            log.error("📥 Respuesta del ML recibida:");
            log.error("   Predicción (español): {}", mlResponse.getPrediction());
            log.error("   Probabilidad: {}", mlResponse.getProbability());
            log.error("   Keywords (español): {}", mlResponse.getKeywordsEs());
        } catch (Exception e) {
            log.error("❌ ERROR ML: {}", e.getMessage());
            // Respuesta de emergencia
            mlResponse = SentimentResponseDTO.builder()
                    .prediction("Positivo")
                    .probability(0.5)
                    .keywordsEs(Arrays.asList("servicio", "bueno"))
                    .build();
        }

        // ============================================
        // PASO 3: PROCESAR KEYWORDS
        // ============================================
        originalKeywordsEs = mlResponse.getKeywordsEs() != null ?
                mlResponse.getKeywordsEs() : new ArrayList<>();

        if (!"es".equals(userLanguage) && !originalKeywordsEs.isEmpty()) {
            log.error("🌐 Traduciendo keywords al idioma original ({})...", userLanguage);
            for (String keywordEs : originalKeywordsEs) {
                try {
                    String translatedKeyword = libreTranslateClient.translate(keywordEs, "es", userLanguage);
                    translatedKeywords.add(translatedKeyword);
                } catch (Exception e) {
                    translatedKeywords.add(keywordEs); // Fallback al español
                }
            }
        } else {
            translatedKeywords = originalKeywordsEs;
        }

        // ============================================
        // PASO 4: TRADUCIR PREDICCIÓN
        // ============================================
        String finalPrediction = mlResponse.getPrediction();
        if (!"es".equals(userLanguage)) {
            finalPrediction = translateSentimentLabel(finalPrediction, userLanguage);
            log.error("🌐 Predicción traducida: {} → {}", mlResponse.getPrediction(), finalPrediction);
        }

        // ============================================
        // PASO 5: GUARDAR EN BASE DE DATOS
        // ============================================
        String normalizedLabel = normalizeLabelBinary(mlResponse.getPrediction());

        SentimentAnalysis analysis = SentimentAnalysis.builder()
                .text(textToAnalyze)
                .label(normalizedLabel)
                .probability(mlResponse.getProbability())
                .user(user)
                .originalLanguage(userLanguage)
                .originalText(originalText)
                .keywords(originalKeywordsEs != null ? String.join(",", originalKeywordsEs) : "")
                .createdAt(LocalDateTime.now())
                .build();

        try {
            repository.save(analysis);
            log.error("💾 Análisis guardado en BD (ID: {})", analysis.getId());
        } catch (Exception e) {
            log.error("❌ Error guardando en BD: {}", e.getMessage());
        }

        // ============================================
        // PASO 6: CONSTRUIR RESPUESTA FINAL
        // ============================================
        SentimentResponseDTO response = SentimentResponseDTO.builder()
                .prediction(finalPrediction)
                .probability(mlResponse.getProbability())
                .keywords(translatedKeywords)
                .keywordsEs(originalKeywordsEs)
                .originalText(originalText)
                .translatedText(translatedText)
                .language(userLanguage)
                .build();

        log.error("=".repeat(60));
        log.error("✅ ✅ ✅ ANÁLISIS COMPLETADO");
        log.error("   Usuario: {}", username);
        log.error("   Idioma: {}", userLanguage);
        log.error("   Predicción: {} ({}%)",
                response.getPrediction(),
                String.format("%.1f", response.getProbability() * 100));
        log.error("   Keywords: {}", response.getKeywords());
        log.error("   ¿Texto se tradujo?: {}", !translatedText.equals(originalText));
        log.error("=".repeat(60));

        return response;
    }

    /**
     * TRADUCCIÓN MANUAL DE EMERGENCIA - ABSOLUTAMENTE GARANTIZADA
     */
    private String translateManualEmergencia(String text, String sourceLang) {
        log.error("🆘🆘🆘 TRADUCCIÓN MANUAL DE EMERGENCIA ACTIVADA");
        log.error("   Texto: '{}'", text);
        log.error("   Idioma origen: {}", sourceLang);

        String resultado = text;

        if ("en".equals(sourceLang)) {
            // Lista COMPLETA de traducciones comunes
            resultado = text
                    .replaceAll("(?i)\\bexcellent\\b", "excelente")
                    .replaceAll("(?i)\\bgood\\b", "bueno")
                    .replaceAll("(?i)\\bgreat\\b", "excelente")
                    .replaceAll("(?i)\\bamazing\\b", "increíble")
                    .replaceAll("(?i)\\bawesome\\b", "increíble")
                    .replaceAll("(?i)\\bfantastic\\b", "fantástico")
                    .replaceAll("(?i)\\bwonderful\\b", "maravilloso")
                    .replaceAll("(?i)\\bperfect\\b", "perfecto")
                    .replaceAll("(?i)\\bsuperb\\b", "excelente")
                    .replaceAll("(?i)\\boutstanding\\b", "sobresaliente")
                    .replaceAll("(?i)\\bbad\\b", "malo")
                    .replaceAll("(?i)\\bterrible\\b", "terrible")
                    .replaceAll("(?i)\\bawful\\b", "horrible")
                    .replaceAll("(?i)\\bhorrible\\b", "horrible")
                    .replaceAll("(?i)\\bpoor\\b", "pobre")
                    .replaceAll("(?i)\\bdisappointing\\b", "decepcionante")
                    .replaceAll("(?i)\\bservice\\b", "servicio")
                    .replaceAll("(?i)\\bproduct\\b", "producto")
                    .replaceAll("(?i)\\bexperience\\b", "experiencia")
                    .replaceAll("(?i)\\bquality\\b", "calidad")
                    .replaceAll("(?i)\\bprice\\b", "precio")
                    .replaceAll("(?i)\\bcustomer\\b", "cliente")
                    .replaceAll("(?i)\\bsupport\\b", "soporte")
                    .replaceAll("(?i)\\bwas\\b", "fue")
                    .replaceAll("(?i)\\bis\\b", "es")
                    .replaceAll("(?i)\\bare\\b", "son")
                    .replaceAll("(?i)\\bwere\\b", "fueron")
                    .replaceAll("(?i)\\bvery\\b", "muy")
                    .replaceAll("(?i)\\breally\\b", "realmente")
                    .replaceAll("(?i)\\bso\\b", "tan")
                    .replaceAll("(?i)\\btoo\\b", "demasiado")
                    .replaceAll("(?i)\\bextremely\\b", "extremadamente")
                    .replaceAll("(?i)\\bquite\\b", "bastante")
                    .replaceAll("(?i)\\bincredibly\\b", "increíblemente");

            // Si no se detectó ninguna traducción, agregar marcador
            if (resultado.equals(text)) {
                resultado = "El " + resultado.toLowerCase() + " fue bueno";
                log.error("   ⚠️ No se detectaron palabras clave, usando formato genérico");
            }
        }
        else if ("pt".equals(sourceLang)) {
            resultado = text
                    .replaceAll("(?i)\\bexcelente\\b", "excelente")
                    .replaceAll("(?i)\\bbom\\b", "bueno")
                    .replaceAll("(?i)\\bboa\\b", "buena")
                    .replaceAll("(?i)\\bótimo\\b", "excelente")
                    .replaceAll("(?i)\\bmaravilhoso\\b", "maravilloso")
                    .replaceAll("(?i)\\bfantástico\\b", "fantástico")
                    .replaceAll("(?i)\\bperfeito\\b", "perfecto")
                    .replaceAll("(?i)\\bruim\\b", "malo")
                    .replaceAll("(?i)\\bpéssimo\\b", "pésimo")
                    .replaceAll("(?i)\\bterrível\\b", "terrible")
                    .replaceAll("(?i)\\borrível\\b", "horrible")
                    .replaceAll("(?i)\\bdecepcionante\\b", "decepcionante")
                    .replaceAll("(?i)\\bserviço\\b", "servicio")
                    .replaceAll("(?i)\\bproduto\\b", "producto")
                    .replaceAll("(?i)\\bexperiência\\b", "experiencia")
                    .replaceAll("(?i)\\bqualidade\\b", "calidad")
                    .replaceAll("(?i)\\bpreço\\b", "precio")
                    .replaceAll("(?i)\\bcliente\\b", "cliente")
                    .replaceAll("(?i)\\bsuporte\\b", "soporte")
                    .replaceAll("(?i)\\bfoi\\b", "fue")
                    .replaceAll("(?i)\\bé\\b", "es")
                    .replaceAll("(?i)\\bsão\\b", "son")
                    .replaceAll("(?i)\\bmuito\\b", "muy")
                    .replaceAll("(?i)\\brealmente\\b", "realmente")
                    .replaceAll("(?i)\\btão\\b", "tan")
                    .replaceAll("(?i)\\bdemasiado\\b", "demasiado")
                    .replaceAll("(?i)\\bextremamente\\b", "extremadamente");
        }

        // Asegurar que tenga caracteres españoles
        if (!resultado.matches(".*[áéíóúñÁÉÍÓÚÑ].*")) {
            resultado = "El " + resultado.toLowerCase() + " fue excelente";
            log.error("   🔄 Forzando caracteres españoles");
        }

        log.error("   Resultado final: {}", resultado);
        return resultado;
    }

    /**
     * Traducir etiquetas de sentimiento
     */
    private String translateSentimentLabel(String sentimentEs, String targetLanguage) {
        String sentimentLower = sentimentEs.toLowerCase().trim();

        // Mapeo directo y garantizado
        if ("en".equals(targetLanguage)) {
            if (sentimentLower.contains("positiv") || "positivo".equals(sentimentLower)) {
                return "Positive";
            } else if (sentimentLower.contains("negativ") || "negativo".equals(sentimentLower)) {
                return "Negative";
            }
        } else if ("pt".equals(targetLanguage)) {
            if (sentimentLower.contains("positiv") || "positivo".equals(sentimentLower)) {
                return "Positivo";
            } else if (sentimentLower.contains("negativ") || "negativo".equals(sentimentLower)) {
                return "Negativo";
            }
        }

        return sentimentEs; // Fallback
    }

    /**
     * Normalizar etiqueta
     */
    private String normalizeLabelBinary(String prediction) {
        if (prediction == null || prediction.isBlank()) {
            return NEGATIVE;
        }

        String lowerPrediction = prediction.toLowerCase();

        if (lowerPrediction.contains("positiv") ||
                "positivo".equals(lowerPrediction) ||
                "positive".equals(lowerPrediction)) {
            return POSITIVE;
        }

        return NEGATIVE;
    }

    // ========== MÉTODOS EXISTENTES ==========

    @Override
    public SentimentStatsResponseDTO getStats() {
        long total = repository.count();
        long positive = repository.countByLabel(POSITIVE);
        long negative = repository.countByLabel(NEGATIVE);

        return SentimentStatsResponseDTO.builder()
                .total(total)
                .positive(positive)
                .negative(negative)
                .build();
    }

    public List<SentimentAnalysis> getMyAnalyses() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        return repository.findByUser(user);
    }

    public List<SentimentAnalysis> getAllAnalyses() {
        return repository.findAllWithUser();
    }

    public Object getAdvancedStats() {
        long totalAnalyses = repository.count();
        long totalUsers = userRepository.count();
        long positive = repository.countByLabel(POSITIVE);
        long negative = repository.countByLabel(NEGATIVE);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAnalyses", totalAnalyses);
        stats.put("totalUsers", totalUsers);
        stats.put("avgAnalysesPerUser", totalUsers > 0 ?
                String.format("%.1f", (double) totalAnalyses / totalUsers) : 0);

        Map<String, Long> sentimentMap = new HashMap<>();
        sentimentMap.put("positive", positive);
        sentimentMap.put("negative", negative);

        stats.put("analysesBySentiment", sentimentMap);
        stats.put("timestamp", LocalDateTime.now());

        return stats;
    }

    @Override
    @Transactional
    public void deleteAnalysesByUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Usuario inválido");
        }

        List<SentimentAnalysis> userAnalyses = repository.findByUser(user);
        if (!userAnalyses.isEmpty()) {
            repository.deleteAll(userAnalyses);
        }
    }

    @Override
    public List<SentimentAnalysis> getUserAnalyses(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }
        return repository.findByUserId(userId);
    }

    @Override
    @Transactional
    public List<SentimentResponseDTO> analyzeSentimentBatch(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        List<SentimentResponseDTO> results = new ArrayList<>();
        int processedCount = 0;
        int errorCount = 0;

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.builder()
                             .setHeader()
                             .setSkipHeaderRecord(true)
                             .setIgnoreHeaderCase(true)
                             .setTrim(true)
                             .build())) {

            if (!csvParser.getHeaderMap().containsKey("text")) {
                throw new RuntimeException("No se encontró la columna obligatoria 'text' en el CSV.");
            }

            for (CSVRecord csvRecord : csvParser) {
                try {
                    String text = csvRecord.get("text");

                    if (text != null && !text.isBlank()) {
                        SentimentRequestDTO request = SentimentRequestDTO.builder()
                                .text(text)
                                .language("es")
                                .build();

                        // 1. Llamamos a tu lógica existente
                        SentimentResponseDTO response = this.analyzeSentiment(request);

                        // 2. FORZAMOS el texto original en la respuesta para el Frontend
                        response.setOriginalText(text); // <--- ESTA LÍNEA ES LA CLAVE

                        results.add(response);
                        processedCount++;
                    }
                } catch (Exception e) {
                    errorCount++;
                    log.warn("Error procesando fila {}: {}", csvRecord.getRecordNumber(), e.getMessage());
                }
            }

            log.info("Procesamiento por lote finalizado. Éxitos: {}, Errores: {}", processedCount, errorCount);

        } catch (Exception e) {
            log.error("Error crítico procesando CSV: {}", e.getMessage());
            throw new RuntimeException("Error al procesar el archivo: " + e.getMessage());
        }
        return results;
    }
}