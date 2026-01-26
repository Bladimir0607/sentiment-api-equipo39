package com.hackaton.sentiment;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Locale;

/**
 * Prueba de internacionalización.
 * @author Equipo Hackathon Oracle ONE - Backend
 * @version 1.4
 * @since 2026-01-21
 */
public class TestI18n {

    /**
     * Método principal para probar la internacionalización.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(TestConfig.class);

        MessageSource messageSource = context.getBean(MessageSource.class);

        // Probar español
        String es = messageSource.getMessage(
                "error.text.required", null, new Locale("es")
        );
        System.out.println("ES: " + es);

        // Probar inglés
        String en = messageSource.getMessage(
                "error.text.required", null, Locale.ENGLISH
        );
        System.out.println("EN: " + en);

        context.close();
    }
}